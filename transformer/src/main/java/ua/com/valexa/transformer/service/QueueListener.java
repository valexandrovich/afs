package ua.com.valexa.transformer.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.valexa.common.dto.sys.StepRequestDto;
import ua.com.valexa.common.dto.sys.StepResponseDto;

import java.util.concurrent.CompletableFuture;

@Service
public class QueueListener {

    @Autowired
    TransformerService transformerService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "transformer",  errorHandler = "queueListenerErrorHandler")
    public void receiveMessage(StepRequestDto requestDto) {

        CompletableFuture<StepResponseDto> cfuture = transformerService.handleTransform(requestDto);
        cfuture.thenAcceptAsync(this::sendResponse);
    }

    public void sendResponse(StepResponseDto stepResponseDto){
        rabbitTemplate.convertAndSend("scheduler-response", stepResponseDto);
    }

}
