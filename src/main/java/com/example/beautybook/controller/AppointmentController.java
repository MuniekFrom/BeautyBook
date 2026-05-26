package com.example.beautybook.controller;

import com.example.beautybook.dto.AppointmentResponse;
import com.example.beautybook.dto.BookAppointmentRequest;
import com.example.beautybook.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/client/appointments/book")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @Valid @RequestBody BookAppointmentRequest request,
            Authentication authentication
    ) {
        String clientLogin = authentication.getName();
        AppointmentResponse response = appointmentService.bookAppointment(clientLogin, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/client/appointments/me")
    public ResponseEntity<?> getMyAppointments(Authentication authentication) {
        String clientLogin = authentication.getName();
        return ResponseEntity.ok(appointmentService.getMyAppointments(clientLogin));
    }

    @DeleteMapping("/client/appointments/{appointmentId}")
    public ResponseEntity<AppointmentResponse> cancelMyAppointment(
            @PathVariable Long appointmentId,
            Authentication authentication
    ) {
        String clientLogin = authentication.getName();
        AppointmentResponse response = appointmentService.cancelMyAppointment(clientLogin, appointmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/appointments/me")
    public ResponseEntity<?> getMyEmployeeAppointments(Authentication authentication) {
        String employeeLogin = authentication.getName();
        return ResponseEntity.ok(appointmentService.getMyEmployeeAppointments(employeeLogin));
    }

    @PatchMapping("/employee/appointments/{appointmentId}/complete")
    public ResponseEntity<AppointmentResponse> completeAppointment(
            @PathVariable Long appointmentId,
            Authentication authentication
    ) {
        String employeeLogin = authentication.getName();
        AppointmentResponse response = appointmentService.completeAppointment(employeeLogin, appointmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/appointments")
    public ResponseEntity<?> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }
}