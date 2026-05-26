package com.example.beautybook.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/client/test")
    public String clientTest() {
        return "Hello CLIENT - token działa";
    }

    @GetMapping("/admin/test")
    public String adminTest() {
        return "Hello ADMIN - token działa";
    }

    @GetMapping("/employee/test")
    public String employeeTest() {
        return "Hello EMPLOYEE - token działa";
    }
}