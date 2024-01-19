package ua.com.valexa.webbackend.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ua.com.valexa.common.dto.sys.StoredJobRequestDto;
import ua.com.valexa.db.model.sys.Job;
import ua.com.valexa.db.model.sys.StoredJob;
import ua.com.valexa.db.repository.sys.JobRepository;
import ua.com.valexa.db.repository.sys.StoredJobRepository;

import java.util.List;

@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

    @Autowired
    StoredJobRepository storedJobRepository;

    @Autowired
    JobRepository jobRepository;

    @Value("${scheduler-init-queue}")
    private String schedulerInitQueue;

    @Autowired
    RabbitTemplate rabbitTemplate;


    @GetMapping("/stored-jobs")
    public List<StoredJob> getAllStoredJobs(){
        return storedJobRepository.findAll();
    }

    @GetMapping("/jobs")
    public List<Job> getAllJobs(){
        return jobRepository.findAll();
    }

    @PostMapping("/init")
    public void initStoredJob(@RequestBody StoredJobRequestDto dto){
        System.out.println(dto);
        rabbitTemplate.convertAndSend(schedulerInitQueue, dto);
    }


}
