package ua.com.valexa.webbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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


}
