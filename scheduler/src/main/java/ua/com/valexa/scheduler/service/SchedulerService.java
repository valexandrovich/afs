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

            // TODO Remove service queues hardcoding
            switch (ss.getServiceName()) {
                case "downloader": {
                    log.debug("Sending to : " + "downloader : " + stepRequestDto);
                    rabbitTemplate.convertAndSend("downloader", stepRequestDto);
                    break;
                }
                case "importer": {
                    log.debug("Sending to : " + "importer : " + stepRequestDto);
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

    private StoredStep getFirstStep(StoredJob sj) throws RuntimeException {
        Optional<StoredStep> firstStep = sj.getSteps().stream()
                .filter(StoredStep::getIsEnabled)
                .min(Comparator.comparingInt(StoredStep::getStepOrder));
        if (firstStep.isPresent()) {
            return firstStep.get();
        } else {
            throw new RuntimeException("Cant find first enabled step in StoredJob: " + sj);
        }
    }

    private StoredStep findNextStep(Integer previousStep, StoredJob storedJob) {
        return storedJob.getSteps().stream()
                .filter(storedStep -> storedStep.getStepOrder() > previousStep && storedStep.getIsEnabled())
                .min(Comparator.comparingInt(StoredStep::getStepOrder))
                .orElse(null);
    }

    public void handleStepResponse(StepResponseDto stepResponseDto) {
        log.debug("Handling StepResponseDto: " + stepResponseDto.toString().replaceAll("\n", " ").replaceAll("\r", " "));

        try {
            Step step = stepRepository.findById(stepResponseDto.getStepId()).orElseThrow(() -> new RuntimeException("Can't find Step with id : " + stepResponseDto.getStepId()));
            step.setFinishedAt(LocalDateTime.now());
            step = stepRepository.save(step);
            Job job = step.getJob();
            job.getResults().putAll(stepResponseDto.getResults());
            jobRepository.save(job);

            if (step.getStatus().equals(StepStatus.FAILED)) {
                if (step.getStoredStep().getIsSkipable()) {
                    step.setStatus(StepStatus.SKIPED);
                    step = stepRepository.save(step);
                } else {
                    log.debug("Job failed on non-skippable step" + job.getStoredJob().getId());
                    job.setFinishedAt(LocalDateTime.now());
                    job = jobRepository.save(job);
                    return;
                }
            }


            StoredStep nextStoredStep = findNextStep(step.getStoredStep().getStepOrder(), job.getStoredJob());

            if (nextStoredStep != null) {
                Step nextStep = new Step();
                nextStep.setJob(job);
                nextStep.setStatus(StepStatus.NEW);
                nextStep.setStartedAt(LocalDateTime.now());
                nextStep.setStoredStep(nextStoredStep);
                nextStep = stepRepository.save(nextStep);

                StepRequestDto stepRequestDto = new StepRequestDto();
                stepRequestDto.setWorkerName(nextStoredStep.getWorkerName());
                stepRequestDto.setParameters(nextStoredStep.getParameters());
                stepRequestDto.getParameters().putAll(stepResponseDto.getResults());
                stepRequestDto.setId(nextStep.getId());

                // TODO replace queues to service hardcoding
                switch (nextStoredStep.getServiceName()) {
                    case "downloader": {
                        log.debug("Sending to : " + "downloader : " + stepRequestDto);
                        rabbitTemplate.convertAndSend("downloader", stepRequestDto);
                        break;
                    }
                    case "importer": {
                        log.debug("Sending to : " + "importer : " + stepRequestDto);
                        rabbitTemplate.convertAndSend("importer", stepRequestDto);
                        break;
                    }
                    default: {
                        log.error("Can't find desired service for handling step: " + step);
                    }
                }

            } else {
                log.debug("Can't find next step in Stored Job: " + job.getStoredJob().getId());
                job.setFinishedAt(LocalDateTime.now());
                job = jobRepository.save(job);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}

