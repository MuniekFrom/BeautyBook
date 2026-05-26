package com.example.beautybook.controller;

import com.example.beautybook.dto.AvailabilitySlotResponse;
import com.example.beautybook.dto.CreateAvailabilitySlotRequest;
import com.example.beautybook.dto.CreateMyAvailabilitySlotRequest;
import com.example.beautybook.service.AvailabilitySlotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AvailabilitySlotController {

    private final AvailabilitySlotService availabilitySlotService;

    public AvailabilitySlotController(AvailabilitySlotService availabilitySlotService) {
        this.availabilitySlotService = availabilitySlotService;
    }

    @GetMapping("/slots/available")
    public ResponseEntity<?> getAvailableSlots() {
        return ResponseEntity.ok(availabilitySlotService.getAvailableSlots());
    }

    @GetMapping("/slots/available/service/{serviceId}")
    public ResponseEntity<?> getAvailableSlotsByService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(availabilitySlotService.getAvailableSlotsByService(serviceId));
    }

    @GetMapping("/slots/available/employee/{employeeProfileId}")
    public ResponseEntity<?> getAvailableSlotsByEmployee(@PathVariable Long employeeProfileId) {
        return ResponseEntity.ok(availabilitySlotService.getAvailableSlotsByEmployee(employeeProfileId));
    }

    @GetMapping("/employee/slots/{employeeProfileId}")
    public ResponseEntity<?> getSlotsByEmployee(@PathVariable Long employeeProfileId) {
        return ResponseEntity.ok(availabilitySlotService.getSlotsByEmployee(employeeProfileId));
    }

    @GetMapping("/employee/slots/me")
    public ResponseEntity<?> getMySlots(Authentication authentication) {
        String employeeLogin = authentication.getName();
        return ResponseEntity.ok(availabilitySlotService.getMySlots(employeeLogin));
    }

    @PostMapping("/employee/slots")
    public ResponseEntity<AvailabilitySlotResponse> createMySlot(
            @Valid @RequestBody CreateMyAvailabilitySlotRequest request,
            Authentication authentication
    ) {
        String employeeLogin = authentication.getName();
        AvailabilitySlotResponse createdSlot = availabilitySlotService.createMySlot(employeeLogin, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSlot);
    }

    @PostMapping("/admin/slots")
    public ResponseEntity<AvailabilitySlotResponse> createSlot(
            @Valid @RequestBody CreateAvailabilitySlotRequest request
    ) {
        AvailabilitySlotResponse createdSlot = availabilitySlotService.createSlot(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSlot);
    }
}