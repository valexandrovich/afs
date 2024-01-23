package ua.com.valexa.scheduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ua.com.valexa.common.dto.sys.StepResponseDto;
import ua.com.valexa.common.dto.sys.StoredJobRequestDto;

@Service
@Slf4j
public class QueueListener {
    final ObjectMapper objectMapper;
    final SchedulerService service;

    public QueueListener(ObjectMapper objectMapper, SchedulerService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @RabbitListener(queues = "${scheduler-init-queue}", errorHandler = "queueListenerErrorHandler")
    public void receiveInitMessage(StoredJobRequestDto storedJobRequestDto) {
        log.info("Scheduler got StoredJobRequestDto: " + storedJobRequestDto.toString().replaceAll("\n", " ").replaceAll("\r", " "));
        try {
            service.initStoredJob(storedJobRequestDto);
        } catch (Exception e){
            // TODO catch errors from executor
            log.error(e.getMessage());
        }
    }

    @RabbitListener(queues = "${scheduler-response-queue}", errorHandler = "queueListenerErrorHandler")
    public void receiveResponseMessage(StepResponseDto stepResponseDto) {
        log.info("Scheduler got StepResponseDto: " + stepResponseDto.toString().replaceAll("\n", " ").replaceAll("\r", " "));
        service.handleStepResponse(stepResponseDto);
    }
}
