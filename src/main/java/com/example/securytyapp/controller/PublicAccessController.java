package com.example.securytyapp.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicAccessController {

    @ResponseStatus(code = org.springframework.http.HttpStatus.OK)
    @RequestMapping("/hello")
    public String sayHello(){
        return ">>> Hey Guest!, You can access this page!, because it's public!";
    }

}
