package ua.com.valexa.scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.valexa.common.dto.sys.StepRequestDto;
import ua.com.valexa.common.dto.sys.StepResponseDto;
import ua.com.valexa.common.dto.sys.StoredJobRequestDto;
import ua.com.valexa.db.model.enums.StepStatus;
import ua.com.valexa.db.model.sys.Job;
import ua.com.valexa.db.model.sys.Step;
import ua.com.valexa.db.model.sys.StoredJob;
import ua.com.valexa.db.model.sys.StoredStep;
import ua.com.valexa.db.repository.sys.JobRepository;
import ua.com.valexa.db.repository.sys.StepRepository;
import ua.com.valexa.db.repository.sys.StoredJobRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

@Service
@Slf4j
public class SchedulerService {

    @Autowired
    StoredJobRepository storedJobRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    StepRepository stepRepository;



    public void initStoredJob(StoredJobRequestDto dto) {
        log.debug("Initing Stored job: " + dto.getStoredJobId());
        try {
            StoredJob sj = storedJobRepository.findById(dto.getStoredJobId()).orElseThrow(() -> new RuntimeException("Cant find StoredJob with id: " + dto.getStoredJobId()));

            Job job = new Job();
            job.setStoredJob(sj);
            job.setStartedAt(LocalDateTime.now());
            job.setInitiatorName(dto.getInitiatorName());
            job = jobRepository.save(job);

            StoredStep ss = getFirstStep(sj);
            Step step = new Step();
            step.setJob(job);
            step.setStatus(StepStatus.NEW);
            step.setStartedAt(LocalDateTime.now());
            step.setStoredStep(ss);
            step = stepRepository.save(step);

            StepRequestDto stepRequestDto = new StepRequestDto();
            stepRequestDto.setWorkerName(ss.getWorkerName());
            stepRequestDto.setParameters(ss.getParameters());
            stepRequestDto.setId(step.getId());


            switch (ss.getServiceName()) {
                case "downloader": {
                    log.debug("Sending to : " + "downloader : "  + stepRequestDto);
                    rabbitTemplate.convertAndSend("downloader", stepRequestDto);
                    break;
                }
                case "importer": {
                    log.debug("Sending to : " + "importer : "  + stepRequestDto);
                    rabbitTemplate.convertAndSend("importer", stepRequestDto);
                    break;
                }
                default: {
                    log.info("Can't find desired service for handling step: " + step);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public StoredStep getFirstStep(StoredJob sj) {
        Optional<StoredStep> firstStep = sj.getSteps().stream()
                .filter(StoredStep::getIsEnabled) // filter only enabled steps
                .min(Comparator.comparingInt(StoredStep::getStepOrder)); // find the one with the smallest step order
        if (firstStep.isPresent()) {
            return firstStep.get();
        } else {
            throw new RuntimeException("Cant find first enabled step in StoredJob: " + sj);
        }

    }

    private StoredStep findNextStep(Integer previousStep, StoredJob storedJob){
        return storedJob.getSteps().stream()
                .filter(storedStep -> storedStep.getStepOrder() > previousStep && storedStep.getIsEnabled())
                .min(Comparator.comparingInt(StoredStep::getStepOrder))
                .orElse(null);
    }

    public void handleNextStep(StepResponseDto stepResponseDto) {

        try {
            System.out.println(LocalDateTime.now().toString() + " : " );
            Step step = stepRepository.findById(stepResponseDto.getStepId()).orElseThrow(() -> new RuntimeException("Can't find Step with id : " + stepResponseDto.getStepId()));
            Job job = step.getJob();
            job.getResults().putAll(stepResponseDto.getResults());
            jobRepository.save(job);


            StoredStep nextStoredStep = findNextStep(step.getStoredStep().getStepOrder(), job.getStoredJob());

            if (nextStoredStep != null){

                Step nextStep = new Step();
                nextStep.setJob(job);
                nextStep.setStatus(StepStatus.NEW);
                nextStep.setStartedAt(LocalDateTime.now());
                nextStep.setStoredStep(nextStoredStep);
                nextStep = stepRepository.save(step);

                StepRequestDto stepRequestDto = new StepRequestDto();
                stepRequestDto.setWorkerName(nextStoredStep.getWorkerName());
                stepRequestDto.setParameters(nextStoredStep.getParameters());
                stepRequestDto.getParameters().putAll(stepResponseDto.getResults());
                stepRequestDto.setId(nextStep.getId());

                switch (nextStoredStep.getServiceName()) {
                    case "downloader": {
                        log.debug("Sending to : " + "downloader : "  + stepRequestDto);
                        rabbitTemplate.convertAndSend("downloader", stepRequestDto);
                        break;
                    }
                    case "importer": {
                        log.debug("Sending to : " + "importer : "  + stepRequestDto);
                        rabbitTemplate.convertAndSend("importer", stepRequestDto);
                        break;
                    }
                    default: {
                        log.info("Can't find desired service for handling step: " + step);
                    }
                }

            }

            log.debug("Looking for next step");

//            step.setStatus(stepResponseDto.getStatus());
//            step.setComment(stepResponseDto.getComment());
//            step.setFinishedAt(LocalDateTime.now());
//            stepRepository.save(step);





        } catch (Exception e){
            log.error(e.getMessage());
        }



    }
}

