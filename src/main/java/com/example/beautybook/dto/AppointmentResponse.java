package com.example.beautybook.dto;

import com.example.beautybook.model.enums.AppointmentStatus;

import java.time.LocalDateTime;

public class AppointmentResponse {

    private Long id;
    private String clientLogin;
    private String serviceName;
    private String employeeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatus status;
    private String notes;

    public AppointmentResponse(Long id, String clientLogin, String serviceName, String employeeName, LocalDateTime startTime, LocalDateTime endTime, AppointmentStatus status, String notes) {
        this.id = id;
        this.clientLogin = clientLogin;
        this.serviceName = serviceName;
        this.employeeName = employeeName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public String getClientLogin() {
        return clientLogin;
    }

    public String getServiceName() {
        return serviceName;
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

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }
}