package com.example.beautybook.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CreateAvailabilitySlotRequest {

    @NotNull(message = "Employee profile id is required")
    private Long employeeProfileId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    public CreateAvailabilitySlotRequest() {
    }

    public Long getEmployeeProfileId() {
        return employeeProfileId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEmployeeProfileId(Long employeeProfileId) {
        this.employeeProfileId = employeeProfileId;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}