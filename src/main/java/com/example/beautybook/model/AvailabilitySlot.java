package com.example.beautybook.model;

import com.example.beautybook.model.enums.SlotStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "availability_slots")
public class AvailabilitySlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotStatus status;

    @ManyToOne
    @JoinColumn(name = "employee_profile_id", nullable = false)
    private EmployeeProfile employeeProfile;

    public AvailabilitySlot() {
    }

    public AvailabilitySlot(LocalDateTime startTime, LocalDateTime endTime, SlotStatus status, EmployeeProfile employeeProfile) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.employeeProfile = employeeProfile;
    }

    public Long getId() {
        return id;
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

    public EmployeeProfile getEmployeeProfile() {
        return employeeProfile;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStatus(SlotStatus status) {
        this.status = status;
    }

    public void setEmployeeProfile(EmployeeProfile employeeProfile) {
        this.employeeProfile = employeeProfile;
    }
}