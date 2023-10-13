package com.example.securytyapp.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private")
@PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
public class PrivateAccessController {

    @GetMapping("/say_hello")
    public String sayHello(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ">>> Hey "+username+", You can access this page!";
    }

    @GetMapping("/user")
    public String userAccess(){
        return ">>> Hey User!, You can access this page!";
    }

    @GetMapping("/mod")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public String moderatorAccess(){
        return ">>> Hey Moderator!, You can access this page!";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess(){
        return ">>> HEY ADMIN!, You can access this page!";
    }
}
