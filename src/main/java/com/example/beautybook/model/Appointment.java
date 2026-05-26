package com.example.beautybook.model;

import com.example.beautybook.model.enums.AppointmentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User client;

    @ManyToOne
    private BeautyService beautyService;

    @OneToOne
    private AvailabilitySlot availabilitySlot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    private String notes;

    private LocalDateTime createdAt;

    public Appointment() {
    }

    public Appointment(User client, BeautyService beautyService, AvailabilitySlot availabilitySlot, AppointmentStatus status, String notes, LocalDateTime createdAt) {
        this.client = client;
        this.beautyService = beautyService;
        this.availabilitySlot = availabilitySlot;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public User getClient() {
        return client;
    }

    public BeautyService getBeautyService() {
        return beautyService;
    }

    public AvailabilitySlot getAvailabilitySlot() {
        return availabilitySlot;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public void setBeautyService(BeautyService beautyService) {
        this.beautyService = beautyService;
    }

    public void setAvailabilitySlot(AvailabilitySlot availabilitySlot) {
        this.availabilitySlot = availabilitySlot;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}