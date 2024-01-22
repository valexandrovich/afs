package ua.com.valexa.importer.service.govua;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.com.valexa.common.dto.sys.StepResponseDto;
import ua.com.valexa.common.dto.sys.StepUpdateDto;
import ua.com.valexa.db.model.enums.StepStatus;
import ua.com.valexa.importer.service.Importable;

import java.util.Map;

@Service("govua01")
@Slf4j
public class Govua01Importer implements Importable {

    @Autowired
    JobLauncher jobLauncher;


    @Autowired
    RabbitTemplate rabbitTemplate;


    @Value("${cpms-queue}")
    private String cpmsQueue;


    @Autowired
    @Qualifier("govua01job")
    Job job;


    @Override
    public StepResponseDto handleImport(Long stepId, Map<String, String> parameters)
    {
        System.out.println("Import " + stepId + " " + parameters);
        StepUpdateDto stepUpdateDto = new StepUpdateDto();
        stepUpdateDto.setStepId(stepId);
        stepUpdateDto.setStatus(StepStatus.IN_PROCESS);
        stepUpdateDto.setProgress(0.0);
        stepUpdateDto.setComment("Початок імпорту");
        sendUpdate(stepUpdateDto);

        StepResponseDto stepResponseDto = new StepResponseDto();
        stepResponseDto.setStepId(stepId);

        JobParameters jp = new JobParametersBuilder()
                .addString("file", parameters.get("file"))
                .addString("stepId", String.valueOf(stepId))
                .toJobParameters();

        stepUpdateDto.setComment("Підготовка параметрів для Spring Batch Job");
        sendUpdate(stepUpdateDto);

        try {
            JobExecution jobExecution =  jobLauncher.run(job, jp);

            if (jobExecution.getStatus().equals(BatchStatus.COMPLETED)){
                stepUpdateDto.setComment("Файл імпортовано");
                stepUpdateDto.setProgress(1);
                stepUpdateDto.setStatus(StepStatus.FINISHED);
                sendUpdate(stepUpdateDto);
            } else {
                stepResponseDto.setStatus(StepStatus.FAILED);
                stepUpdateDto.setComment("Статус: " + jobExecution.getStatus());
                stepUpdateDto.setStatus(StepStatus.FAILED);
                sendUpdate(stepUpdateDto);
                log.error(jobExecution.toString());
            }

        } catch (Exception e){
            stepResponseDto.setStatus(StepStatus.FAILED);
            stepUpdateDto.setComment(e.getMessage());
            stepUpdateDto.setStatus(StepStatus.FAILED);
            sendUpdate(stepUpdateDto);
            log.error(e.getMessage());
        }



        return stepResponseDto;
    }

    private void sendUpdate(StepUpdateDto stepUpdateDto){
        rabbitTemplate.convertAndSend(cpmsQueue, stepUpdateDto);
    }
}
