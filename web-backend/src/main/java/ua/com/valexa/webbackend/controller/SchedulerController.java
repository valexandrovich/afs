package ua.com.valexa.webbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.com.valexa.common.dto.sys.StoredJobRequestDto;
import ua.com.valexa.db.model.sys.StoredJob;
import ua.com.valexa.db.repository.sys.StoredJobRepository;

import java.util.List;

@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

    @Autowired
    StoredJobRepository storedJobRepository;


    @GetMapping("/stored-jobs")
    public List<StoredJob> getAllStoredJobs(){
        return storedJobRepository.findAll();
    }

    @PostMapping("/init")
    public void initStoredJob(@RequestBody StoredJobRequestDto dto){
        System.out.println(dto);
    }


}
