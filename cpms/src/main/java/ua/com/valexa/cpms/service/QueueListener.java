package ua.com.valexa.cpms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.valexa.common.dto.sys.StepUpdateDto;
import ua.com.valexa.db.model.enums.StepStatus;
import ua.com.valexa.db.model.sys.Step;
import ua.com.valexa.db.repository.sys.StepRepository;

import java.time.LocalDateTime;


@Service
@Slf4j
public class QueueListener {


    @Autowired
    StepRepository stepRepository;

    @RabbitListener(queues = "${cpms-queue}", errorHandler = "queueListenerErrorHandler")
    public void receiveInitMessage(StepUpdateDto stepUpdateDto) {
        try {
            System.out.println(LocalDateTime.now().toString() + " : " + stepUpdateDto);
            Step s = stepRepository.findById(stepUpdateDto.getStepId()).orElseThrow(()->new RuntimeException("Can't find step with id: " + stepUpdateDto.getStepId()));

            if (s.getStatus().equals(StepStatus.IN_PROCESS) || s.getStatus().equals(StepStatus.NEW)){
                s.setComment(stepUpdateDto.getComment());
                s.setStatus(stepUpdateDto.getStatus());
                s.setProgress(stepUpdateDto.getProgress());
                stepRepository.save(s);
            }

        }
        catch (Exception e){
            log.error(e.getMessage());
        }



    }
}
