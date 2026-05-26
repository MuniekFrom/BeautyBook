package com.example.beautybook.controller;

import com.example.beautybook.dto.CreateEmployeeWithProfileRequest;
import com.example.beautybook.dto.EmployeeProfileResponse;
import com.example.beautybook.service.AdminEmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminEmployeeController {

    private final AdminEmployeeService adminEmployeeService;

    public AdminEmployeeController(AdminEmployeeService adminEmployeeService) {
        this.adminEmployeeService = adminEmployeeService;
    }

    @PostMapping("/admin/employees/create")
    public ResponseEntity<EmployeeProfileResponse> createEmployeeWithProfile(
            @Valid @RequestBody CreateEmployeeWithProfileRequest request
    ) {
        EmployeeProfileResponse response = adminEmployeeService.createEmployeeWithProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}