package com.example.beautybook.dto;

import com.example.beautybook.model.enums.SlotStatus;

import java.time.LocalDateTime;

public class AvailabilitySlotResponse {

    private Long id;
    private Long employeeProfileId;
    private String employeeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SlotStatus status;

    public AvailabilitySlotResponse(Long id, Long employeeProfileId, String employeeName, LocalDateTime startTime, LocalDateTime endTime, SlotStatus status) {
        this.id = id;
        this.employeeProfileId = employeeProfileId;
        this.employeeName = employeeName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getEmployeeProfileId() {
        return employeeProfileId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public SlotStatus getStatus() {
        return status;
    }
}