package com.smart_finance_manager_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public String testingController(){
        return "Hii I am for testing purpose";
    }
}
