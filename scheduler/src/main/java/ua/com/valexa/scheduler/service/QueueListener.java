package ua.com.valexa.scheduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        log.debug("Scheduler get message: " + storedJobRequestDto.toString().replaceAll("\n", " ").replaceAll("\r", " "));
        try {
            service.initStoredJob(storedJobRequestDto);
        } catch (Exception e){
            // TODO catch errors from executor
            log.error(e.getMessage());
        }
    }

    @RabbitListener(queues = "${scheduler-response-queue}", errorHandler = "queueListenerErrorHandler")
    public void receiveResponseMessage(StepResponseDto stepResponseDto) {
        log.debug("Scheduler got StepResponseDto: " + stepResponseDto);
        service.handleNextStep(stepResponseDto);
    }
}
