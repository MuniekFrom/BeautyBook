package com.example.beautybook.repository;

import com.example.beautybook.model.Appointment;
import com.example.beautybook.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClientLogin(String login);

    List<Appointment> findByAvailabilitySlotEmployeeProfileId(Long employeeProfileId);

    boolean existsByAvailabilitySlotIdAndStatus(Long availabilitySlotId, AppointmentStatus status);

    long countByStatus(AppointmentStatus status);
}