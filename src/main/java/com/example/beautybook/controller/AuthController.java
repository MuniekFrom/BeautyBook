package com.example.beautybook.controller;

import com.example.beautybook.dto.LoginRequest;
import com.example.beautybook.dto.LoginResponse;
import com.example.beautybook.dto.MeResponse;
import com.example.beautybook.dto.RegisterEmployeeRequest;
import com.example.beautybook.dto.RegisterRequest;
import com.example.beautybook.dto.RegisterResponse;
import com.example.beautybook.dto.UserResponse;
import com.example.beautybook.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.beautybook.dto.ChangePasswordRequest;

@RestController
@RequestMapping
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/register/client")
    public ResponseEntity<RegisterResponse> registerClient(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.registerClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth/me")
    public ResponseEntity<MeResponse> getCurrentUser(Authentication authentication) {
        String login = authentication.getName();
        MeResponse response = authService.getCurrentUser(login);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/employees/register")
    public ResponseEntity<UserResponse> registerEmployee(@Valid @RequestBody RegisterEmployeeRequest request) {
        UserResponse response = authService.registerEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/auth/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {
        String login = authentication.getName();
        authService.changePassword(login, request);
        return ResponseEntity.ok().body("Password changed successfully");
    }
}