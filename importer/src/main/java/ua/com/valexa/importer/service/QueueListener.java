package ua.com.valexa.importer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class QueueListener {

    final ObjectMapper objectMapper;
    final ImporterService service;

    @Autowired
    RabbitTemplate rabbitTemplate;

    public QueueListener(ObjectMapper objectMapper, ImporterService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @RabbitListener(queues = "${importer-queue}", errorHandler = "queueListenerErrorHandler")
    public void receiveInitMessage(StepRequestDto stepRequestDto) {
        log.debug("Importer get message: " + stepRequestDto.toString().replaceAll("\n", " ").replaceAll("\r", " "));
         CompletableFuture<StepResponseDto> cfuture = service.handleImport(stepRequestDto);
         cfuture.thenAcceptAsync(this::sendResponse);
    }


    public void sendResponse(StepResponseDto stepResponseDto){
        rabbitTemplate.convertAndSend("scheduler-response", stepResponseDto);
    }

}
