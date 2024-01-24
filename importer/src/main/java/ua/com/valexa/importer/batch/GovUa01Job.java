package ua.com.valexa.importer.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileUrlResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.PlatformTransactionManager;
import ua.com.valexa.common.dto.red.GovUa01Dto;
import ua.com.valexa.common.dto.sys.StepUpdateDto;
import ua.com.valexa.db.model.enums.StepStatus;
import ua.com.valexa.db.model.red.GovUa01;
import ua.com.valexa.importer.mapper.GovUa01Mapper;

import javax.sql.DataSource;
import java.io.*;
import java.net.MalformedURLException;
import java.util.UUID;

@Configuration
@EnableBatchProcessing
@Slf4j
public class GovUa01Job {

    @Value("${cpms-queue}")
    private String cpmsQueue;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    RabbitTemplate rabbitTemplate;

    private File inputFile;

    @Autowired
    private ItemReader<GovUa01Dto> govUa01reader;

    @Autowired
    private ItemProcessor<GovUa01Dto, GovUa01> govUa01Processor;

    @Autowired
    private JdbcBatchItemWriter<GovUa01> govUa01writer;

    @Autowired
    ItemWriteListener<GovUa01> govUa01writerListener;


    private final int skipLines = 1;
    private int totalRowsCount;
    private long handledRowsCount;
    private long duplicateRowsCount;
    private long errorsCount;


    @Bean
    @StepScope
    public FlatFileItemReader<GovUa01Dto> govUa01reader(@Value("#{jobParameters['file']}") String file) {
        try {
            return new FlatFileItemReaderBuilder<GovUa01Dto>()
                    .resource(new FileUrlResource(file))
                    .name("govua01reader")
                    .delimited()
                    .delimiter("\t")
                    .names("number", "date", "type", "firm_edrpou", "firm_name", "case_number", "start_date_auc", "end_date_auc", "court_name", "end_registration_date")
                    .targetType(GovUa01Dto.class)
                    .linesToSkip(skipLines)
                    .build();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    @Bean
    @StepScope
    public ItemProcessor<GovUa01Dto, GovUa01> govUa01processor(@Value("#{jobParameters['stepId']}") String stepId) {
        return new ItemProcessor<GovUa01Dto, GovUa01>() {

            @Autowired
            GovUa01Mapper mapper;

            @Override
            public GovUa01 process(GovUa01Dto item) throws Exception {
                GovUa01 result = mapper.mapToEntity(item);
                result.setRevisionId(Long.valueOf(stepId));
                result.setId(UUID.randomUUID());
                return result;
            }
        };
    }


    @Bean
    @StepScope
    public JdbcBatchItemWriter<GovUa01> govUa01writer() {
        return new JdbcBatchItemWriterBuilder<GovUa01>()
                .sql("insert into red.govua_01 (" +
                        "id, number, date, type, firm_edrpou, case_number, court_name, created_at, revision_id, end_date_auc, end_registration_date, firm_name, hash, start_date_auc) " +
                        "VALUES (:id, :number, :date, :type, :firmEdrpou, :caseNumber, :courtName, :createdAt, :revisionId, :endDateAuc, :endRegistrationDate, :firmName, :hash, :startDateAuc) ")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .dataSource(dataSource)
                .assertUpdates(false)
                .build();
    }

    @Bean
    @StepScope
    public ItemWriteListener<GovUa01> govUa01writerListener(@Value("#{jobParameters['stepId']}") String stepId) {
        return new ItemWriteListener<GovUa01>() {
            @Override
            public void afterWrite(Chunk<? extends GovUa01> items) {
                handledRowsCount += items.size();
            }

            @Override
            public void beforeWrite(Chunk<? extends GovUa01> items) {

                long divident = handledRowsCount + duplicateRowsCount + errorsCount;
                double progress = (double) divident / totalRowsCount;

                StepUpdateDto stepUpdateDto = new StepUpdateDto();
                stepUpdateDto.setStepId(Long.valueOf(stepId));
                stepUpdateDto.setProgress(progress);
                stepUpdateDto.setComment("Опрацьовано: " + handledRowsCount + " записів;  Дублікатів: " + duplicateRowsCount + "; Помилок: " + errorsCount + ";");
                stepUpdateDto.setStatus(StepStatus.IN_PROCESS);
                sendUpdate(stepUpdateDto);
            }

            @Override
            public void onWriteError(Exception exception, Chunk<? extends GovUa01> items) {
                if (exception instanceof DuplicateKeyException) {
                    duplicateRowsCount++;
                }
            }
        };
    }

    @Bean
    @StepScope
    public SkipPolicy govuUa01skipPolicy() {
        return (t, skipCount) -> {

            if (!(t instanceof DuplicateKeyException)) {
                errorsCount++;
            }
            return true;
        };
    }

    @Bean
    public Step govUa01step() {
        return new StepBuilder("Govua01step", jobRepository)
                .<GovUa01Dto, GovUa01>chunk(1000, transactionManager)
                .reader(govUa01reader)
                .processor(govUa01Processor)
                .writer(govUa01writer())
                .listener(govUa01writerListener)
                .listener(govUa01stepExecutionListener())
                .faultTolerant()
                .skipPolicy(govuUa01skipPolicy())
                .build();
    }

    @Bean
    StepExecutionListener govUa01stepExecutionListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                duplicateRowsCount = 0;
                handledRowsCount = 0;
                errorsCount = 0;
                totalRowsCount = 0;
                inputFile = new File(stepExecution.getJobParameters().getString("file"));
                try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
                    while (br.readLine() != null) {
                        totalRowsCount++;
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                totalRowsCount = totalRowsCount - skipLines;

                StepUpdateDto stepUpdateDto = new StepUpdateDto();
                stepUpdateDto.setStepId(Long.valueOf(stepExecution.getJobParameters().getString("stepId")));
                stepUpdateDto.setStatus(StepStatus.IN_PROCESS);
                stepUpdateDto.setComment("Імпорт даних");
                sendUpdate(stepUpdateDto);

            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                return ExitStatus.COMPLETED;
            }
        };
    }

    @Bean("govua01job")
    public Job govUa01job(JobRepository jobRepository) {
        return new JobBuilder("govUa01job", jobRepository)
                .start(govUa01step())
                .listener(govUa01jobExecutionListener())
                .build();
    }

    @Bean
    public JobExecutionListener govUa01jobExecutionListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                ExecutionContext executionContext = jobExecution.getExecutionContext();
                executionContext.putString("comment", "Опрацьовано: " + handledRowsCount + " записів;  Дублікатів: " + duplicateRowsCount + "; Помилок: " + errorsCount + ";");
            }
        };
    }


    private void sendUpdate(StepUpdateDto stepUpdateDto) {
        rabbitTemplate.convertAndSend(cpmsQueue, stepUpdateDto);
    }


}
