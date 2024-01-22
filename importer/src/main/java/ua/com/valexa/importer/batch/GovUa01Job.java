package ua.com.valexa.importer.batch;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.PlatformTransactionManager;
import ua.com.valexa.common.dto.red.GovUa01Dto;
import ua.com.valexa.db.model.red.GovUa01Row;
import ua.com.valexa.importer.mapper.GovUa01Mapper;

import javax.sql.DataSource;
import java.io.*;

@Configuration
@EnableBatchProcessing
@Slf4j
public class GovUa01Job {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DataSource dataSource;

//    @Bean
//    public FlatFileItemReader<GovUa01Dto> reader() {
//        FlatFileItemReader<GovUa01Dto> reader = new FlatFileItemReader<>();
//        reader.setResource(new ClassPathResource("data.csv"));
//        reader.setLineMapper(new DefaultLineMapper<CsvRow>() {{
//            setLineTokenizer(new DelimitedLineTokenizer() {{
//                setNames("field1", "field2"); // Set your CSV columns here
//            }});
//            setFieldSetMapper(new BeanWrapperFieldSetMapper<CsvRow>() {{
//                setTargetType(CsvRow.class);
//            }});
//        }});
//        return reader;
//    }


//    @Autowired
//    private JobRepository jobRepository;

//    @Autowired
//    private PlatformTransactionManager transactionManager;

//    @Autowired
//    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ItemReader<GovUa01Dto> govUa01reader;

//    @Autowired
//    private ItemProcessor<GovUa01Dto, GovUa01Row> Govua01Processor;

//    @Autowired
//    private ItemWriter<GovUa01Row> Govua01writer;

//    @Autowired
//    private StepExecutionListener Govua01stepExecutionListener;

//    @Autowired
//    private ChunkListener Govua01writerListener;

//    @Autowired
//    private SkipPolicy Govua01skipPolicy;


//    @Autowired
//    private FlatFileItemReader<GovUa01Dto> Govua01reader;

//    @Autowired
//    private ItemProcessor<GovUa01Dto, GovUa01Row> Govua01Processor;

//    @Autowired
//    private JdbcBatchItemWriter<GovUa01Row> Govua01writer;
//    @Autowired
//    private ItemWriteListener<GovUa01Row> Govua01writerListener;

//    @Autowired
//    private SkipPolicy Govua01skipPolicy;

    private final int skipLines = 1;

    private int totalRowsCount;

    private long handledRowsCount;
    private long duplicateRowsCount;
    private long errorsCount;

    private File inputFile;


    @Bean
    @StepScope
    @SneakyThrows
    public FlatFileItemReader<GovUa01Dto> govUa01reader(@Value("#{jobParameters['file']}") String file) {

        System.out.println(file);

        return new FlatFileItemReaderBuilder<GovUa01Dto>()
                .resource(new FileUrlResource(file))
                .name("myreader")
                .delimited()
                .delimiter("\t")
                .names("number", "date", "type", "firm_edrpou", "firm_name", "case_number", "start_date_auc", "end_date_auc", "court_name", "end_registration_date")
                .targetType(GovUa01Dto.class)
                .linesToSkip(skipLines)
                .build();
    }

//    @Bean
//    @StepScope
//    public ItemProcessor<GovUa01Dto, GovUa01Row> Govua01processor() {
//        return new ItemProcessor<GovUa01Dto, GovUa01Row>() {
//
//            @Autowired
//            GovUa01Mapper mapper;
//
//            @Override
//            public GovUa01Row process(GovUa01Dto item) throws Exception {
//                GovUa01Row result = mapper.mapToEntity(item);
//                result.setRevisionId(777L);
//                return result;
//            }
//        };
//    }

    @Bean
    public ItemWriter<GovUa01Dto> writer() {

        System.out.println("Writter");

        return items -> {
            for (GovUa01Dto item : items) {
                System.out.println(item); // Print each item
            }
        };
    }


//    @Bean
//    @StepScope
//    public JdbcBatchItemWriter<GovUa01Row> Govua01writer() {
//        return new JdbcBatchItemWriterBuilder<GovUa01Row>()
//                .sql("insert into red.Govua01 (" +
//                        "number, date, type, firm_edrpou, case_number, court_name, created_at, revision_id, end_date_auc, end_registration_date, firm_name, hash, start_date_auc) " +
//                        "VALUES (:number, :date, :type, :firmEdrpou, :caseNumber, :courtName, :createdAt, :revisionId, :endDateAuc, :endRegistrationDate, :firmName, :hash, :startDateAuc) ")
//                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
//                .dataSource(dataSource)
//                .assertUpdates(false)
//                .build();
//    }

//    @Bean
//    @StepScope
//    public ItemWriteListener<GovUa01Row> Govua01writerListener() {
//        return new ItemWriteListener<GovUa01Row>() {
//            @Override
//            public void afterWrite(Chunk<? extends GovUa01Row> items) {
//                handledRowsCount += items.size();
//            }
//
//            //            @Transactional
//            @Override
//            public void beforeWrite(Chunk<? extends GovUa01Row> items) {
//
////                etlStep.setDescription("Записано: " + handledRowsCount + " Дублікати: " + duplicateRowsCount + " Помилки: " + errorsCount);
////
////                etlStep.setProgress(Double.valueOf((handledRowsCount+duplicateRowsCount)) / totalRowsCount * 100);
////                etlStep = etlStepRepository.save(etlStep);
////                etlStepRepository.flush();
//
////                etlStep.setComment("Опрацьовано: " + handledRowsCount + "  Дублікатів: " + duplicateRowsCount + " Помилок: " + errorsCount);
////                etlStep = etlStepRepository.save(etlStep);
//
//                long divident = handledRowsCount + duplicateRowsCount + errorsCount;
//                double p = (double) divident / totalRowsCount;
//
////                etlStep.setProgress(p);
////                rabbitTemplate.convertAndSend(etlLoggerQueue, etlStep);
////                rabbitTemplate.convertAndSend("afg-etl-logger", etlStep);
//
//                System.out.println("BEFORE WRITE - OK: " + handledRowsCount + "  DUP: " + duplicateRowsCount + " ERRORS:" + errorsCount);
//            }
//
//            @Override
//            public void onWriteError(Exception exception, Chunk<? extends GovUa01Row> items) {
//                if (exception instanceof DuplicateKeyException) {
//                    duplicateRowsCount++;
//                }
//            }
//        };
//    }

    @Bean
    @StepScope
    public SkipPolicy govuUa01skipPolicy() {
        return (t, skipCount) -> {

            if (t instanceof DuplicateKeyException) {
//                    duplicateRows++;
//                    duplicateRowsCount++;
            } else {
//                    parseErrorRows++;
            }
//                EtlSubtask etlSubtask = etlSubtaskRepository.findById(etlSubtaskId).orElseThrow(() -> new EtlTaskNotFoundException(""));
//                etlSubtask.setDescription("SSS");
//                etlSubtaskRepository.save(etlSubtask);

//                    etlSubtask.setDescription("Помилок: " + errorRowsCount);
//                    etlSubtaskRepository.save(etlSubtask);
////                    EtlSubtask etlSubtask = etlSubtaskRepository.findById(etlSubtaskId).get();
////                    etlSubtask.setDescription("Помилок: " + skipCount);
////                    etlSubtaskRepository.save(etlSubtask);
//////                    errorRowsCount.set(skipCount);
//////                    System.out.println("ERR:" + errorRowsCount.get());
//                }

//                errorRowsCount.incrementAndGet();

//                EtlSubtask etlSubtask = etlSubtaskRepository.findById(etlSubtaskId).get();
//                etlSubtask.setModuleName("ERR: " + errorRowsCount.toString());
//                etlSubtaskRepository.save(etlSubtask);
            return true;
        };
    }

//
//    @Bean
//    StepExecutionListener Govua01stepExecutionListener(){
//        return new StepExecutionListener() {
//            @Override
////            @Transactional
//            public void beforeStep(StepExecution stepExecution) {
//
////                long etlSubtaskId = stepExecution.getJobExecution().getJobParameters().getLong("etlSubtaskId");
//                String inputFilePath = stepExecution.getJobExecution().getJobParameters().getString("filename");
////                Long etlTaskId = stepExecution.getJobExecution().getJobParameters().getLong("etlTaskId");
//
////                etlTask = etlTaskRepository.findById(etlTaskId).orElseThrow(null);
//
//                duplicateRowsCount = 0;
//                handledRowsCount = 0;
//                errorsCount = 0;
//                totalRowsCount = 0;
//                inputFile = new File(inputFilePath);
//                try (BufferedReader br = new BufferedReader(new FileReader(inputFile))){
//                    while (br.readLine() != null){
//                        totalRowsCount++;
//                    }
//                } catch (FileNotFoundException e) {
//                    throw new RuntimeException(e);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                totalRowsCount = totalRowsCount - skipLines;
//
//                System.out.println("TOTAL ROWS : " + totalRowsCount);
//
////                etlStep = etlStepRepository.findById(etlSubtaskId).orElseThrow(() -> new EtlTaskNotFoundException(""));
////                etlStep.setStatus(EtlTaskStatus.IN_PROCESS);
////                etlStep.setDescription("Імпорт даних");
////                etlStep =  etlStepRepository.save(etlStep);
////                etlStepRepository.flush();
//            }
//
//            @Override
////            @Transactional
//            public ExitStatus afterStep(StepExecution stepExecution) {
//                System.out.println("AFTER JOB  --  OK: " + handledRowsCount + "  DUP: " + duplicateRowsCount + " ERRORS:" + errorsCount);
////                etlStep.setComment("Опрацьовано: " + handledRowsCount + "  Дублікатів: " + duplicateRowsCount + " Помилок: " + errorsCount);
////                etlStep.setStatus(EtlStatus.COMPLETED);
////                etlStep.setProgress(1.0);
////                etlStep.setFinishedAt(LocalDateTime.now());
////                rabbitTemplate.convertAndSend(etlLoggerQueue, etlStep);
////                rabbitTemplate.convertAndSend("afg-etl-logger", etlStep);
//
////                etlStep.setDescription("Записано: " + handledRowsCount + " Дублікати: " + duplicateRowsCount + " Помилки: " + errorsCount);
////                etlStep.setProgress(Double.valueOf((handledRowsCount+duplicateRowsCount)) / totalRowsCount * 100);
////                etlStep.setFinishedAt(LocalDateTime.now());
////                etlStep.setStatus(EtlTaskStatus.FINISHED);
////                etlStep = etlStepRepository.save(etlStep);
////                etlStepRepository.flush();
//                return ExitStatus.COMPLETED;
//            }
//        };
//    }

//    @Bean
//    public Step Govua01counterStep(){
//        return new StepBuilder("Govua01counterstep", jobRepository)
//                .tasklet(Govua01counterTasklet(), transactionManager)
//                .build();
//    }
//
//    @Bean
//    public Tasklet Govua01counterTasklet(){
//        return (contribution, chunkContext) -> {
//            for (int i = 0; i < 10; i++) {
//                System.out.println(i);
//            }
//            return RepeatStatus.FINISHED;
//        };
//    }

    @Bean
    public Step govUa01step() {
        return new StepBuilder("Govua01step", jobRepository)
                .<GovUa01Dto, GovUa01Dto>chunk(10000, transactionManager)
                .reader(govUa01reader)
//                .skipPolicy(govuUa01skipPolicy())
                .writer(writer())

//                .processor(Govua01Processor)
//                .writer(Govua01writer)
//                .listener(Govua01writerListener)
//                .listener(Govua01stepExecutionListener())
                .faultTolerant()
                .skipPolicy(govuUa01skipPolicy())

                .build();
    }


//    @Bean
//    public Step Govua01stepSendToReport(){
//        return new StepBuilder("Govua01stepSendToReport", jobRepository)
//                .tasklet(Govua01taskletSendToReport(), transactionManager)
//                .build();
//    }

//    @Bean
//    public Tasklet Govua01taskletSendToReport(){
//        return (contribution, chunkContext) -> {
//
//            System.out.println("FINISHED!!!!!!!!!!!!");
//
////            ReportMessage reportMessage = buildReporterMessage();
////            sendToReporter(reportMessage);
//            return RepeatStatus.FINISHED;
//        };
//    }



    @Bean("govua01job")
    public Job govUa01job(JobRepository jobRepository) {
        return new JobBuilder("Govua01job", jobRepository)
//                .listener(govua01jobExecutionListener())
//                .start(Govua01counterStep())
                .start(govUa01step())
//                .next(Govua01stepSendToReport())
                .build();
    }

//    @Bean
//    public JobExecutionListener govua01jobExecutionListener(){
//        return new JobExecutionListener() {
//            @Override
//            public void beforeJob(JobExecution jobExecution) {
//                Long etlStepId = jobExecution.getJobParameters().getLong("etlStepId");
////                etlStep = etlStepRepository.findById(etlStepId).orElseThrow(null);
////                etlStep.setStatus(EtlStatus.IN_PROCESS);
////                etlStep.setComment("Початок імпорту");
////                etlStep = etlStepRepository.save(etlStep);
////                rabbitTemplate.convertAndSend(etlLoggerQueue, etlStep);
////                rabbitTemplate.convertAndSend("antifraud-etl-logger-step", etlStep);
//            }
//
//            @Override
//            public void afterJob(JobExecution jobExecution) {
//                JobExecutionListener.super.afterJob(jobExecution);
//            }
//        };
//    }



//    public void sendToReporter(ReportMessage reportMessage) {
//        log.info("Sending message to REPORTER " + reportMessage.toString());
////        String message = "{\"group\":\"" + importerMessage.getGroup() + "\",\"source\":\"" + importerMessage.getSource() + "\",\"file\":\"" + importerMessage.getFile() + "\"}";
//        rabbitTemplate.convertAndSend("antifraud-report", reportMessage);
//
//    }

//    private ReportMessage buildReporterMessage(){
//        ReportMessage reportMessage = new ReportMessage();
//        reportMessage.setReportId("bankrupts_b2_report");
//        reportMessage.setEtlTaskId(etlStep.getEtlTask().getId());
//        return  reportMessage;
//    }





}
