package com.example.beautybook.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CreateMyAvailabilitySlotRequest {

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    public CreateMyAvailabilitySlotRequest() {
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}