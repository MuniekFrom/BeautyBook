package com.example.beautybook.controller;

import com.example.beautybook.dto.AdminUserResponse;
import com.example.beautybook.service.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/admin/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    @PatchMapping("/admin/users/{userId}/deactivate")
    public ResponseEntity<AdminUserResponse> deactivateUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminUserService.deactivateUser(userId));
    }

    @PatchMapping("/admin/users/{userId}/activate")
    public ResponseEntity<AdminUserResponse> activateUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminUserService.activateUser(userId));
    }

    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<Void> deleteInactiveUser(@PathVariable Long userId) {
        adminUserService.deleteInactiveUser(userId);
        return ResponseEntity.noContent().build();
    }
}