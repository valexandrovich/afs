package ua.com.valexa.importer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.com.valexa.common.dto.sys.StepRequestDto;
import ua.com.valexa.common.dto.sys.StepResponseDto;
import ua.com.valexa.common.dto.sys.StoredJobRequestDto;

@Service
@Slf4j
public class QueueListener {

    final ObjectMapper objectMapper;
    final ImporterService service;

    public QueueListener(ObjectMapper objectMapper, ImporterService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @RabbitListener(queues = "${importer-queue}", errorHandler = "queueListenerErrorHandler")
    public void receiveInitMessage(StepRequestDto stepRequestDto) {
        log.debug("Importer get message: " + stepRequestDto.toString().replaceAll("\n", " ").replaceAll("\r", " "));
        try {
            service.handleImport(stepRequestDto);
        } catch (Exception e){
            // TODO catch errors from executor
            log.error(e.getMessage());
        }
    }

}
