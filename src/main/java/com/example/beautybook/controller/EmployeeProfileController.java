package com.example.beautybook.controller;

import com.example.beautybook.dto.CreateEmployeeProfileRequest;
import com.example.beautybook.dto.EmployeeProfileResponse;
import com.example.beautybook.service.EmployeeProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmployeeProfileController {

    private final EmployeeProfileService employeeProfileService;

    public EmployeeProfileController(EmployeeProfileService employeeProfileService) {
        this.employeeProfileService = employeeProfileService;
    }

    @GetMapping("/employees")
    public ResponseEntity<?> getAllEmployees() {
        return ResponseEntity.ok(employeeProfileService.getAllEmployees());
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeProfileResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeProfileService.getEmployeeById(id));
    }

    @PostMapping("/admin/employees")
    public ResponseEntity<EmployeeProfileResponse> createEmployeeProfile(
            @Valid @RequestBody CreateEmployeeProfileRequest request
    ) {
        EmployeeProfileResponse createdEmployee = employeeProfileService.createEmployeeProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }
}