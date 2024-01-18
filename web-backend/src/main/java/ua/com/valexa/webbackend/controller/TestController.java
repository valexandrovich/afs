package ua.com.valexa.webbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/tst")
    public String getTest(){
        return "Test is work!";
    }

}
