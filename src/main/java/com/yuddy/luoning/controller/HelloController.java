package com.yuddy.luoning.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "Hello! Spring Boot is running!";
    }

    @GetMapping("/api/hello")
    public String helloApi() {
        return "Hello from Online Learning API!";
    }
}