package com.example.beautybook.dto;

import jakarta.validation.constraints.NotNull;

public class BookAppointmentRequest {

    @NotNull(message = "Service id is required")
    private Long serviceId;

    @NotNull(message = "Slot id is required")
    private Long slotId;

    private String notes;

    public BookAppointmentRequest() {
    }

    public Long getServiceId() {
        return serviceId;
    }

    public Long getSlotId() {
        return slotId;
    }

    public String getNotes() {
        return notes;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}