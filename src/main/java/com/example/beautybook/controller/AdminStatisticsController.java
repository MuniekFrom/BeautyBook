package com.example.beautybook.controller;

import com.example.beautybook.dto.AdminStatisticsResponse;
import com.example.beautybook.service.AdminStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminStatisticsController {

    private final AdminStatisticsService adminStatisticsService;

    public AdminStatisticsController(AdminStatisticsService adminStatisticsService) {
        this.adminStatisticsService = adminStatisticsService;
    }

    @GetMapping("/admin/statistics")
    public ResponseEntity<AdminStatisticsResponse> getStatistics() {
        return ResponseEntity.ok(adminStatisticsService.getStatistics());
    }
}