package com.example.beautybook.service;

import com.example.beautybook.dto.AdminStatisticsResponse;
import com.example.beautybook.model.enums.AppointmentStatus;
import com.example.beautybook.repository.AppointmentRepository;
import com.example.beautybook.repository.BeautyServiceRepository;
import com.example.beautybook.repository.EmployeeProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminStatisticsService {

    private final BeautyServiceRepository beautyServiceRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final AppointmentRepository appointmentRepository;

    public AdminStatisticsService(BeautyServiceRepository beautyServiceRepository,
                                  EmployeeProfileRepository employeeProfileRepository,
                                  AppointmentRepository appointmentRepository) {
        this.beautyServiceRepository = beautyServiceRepository;
        this.employeeProfileRepository = employeeProfileRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public AdminStatisticsResponse getStatistics() {

        long servicesCount = beautyServiceRepository.count();
        long employeesCount = employeeProfileRepository.count();
        long appointmentsCount = appointmentRepository.count();

        long bookedAppointmentsCount = appointmentRepository.countByStatus(AppointmentStatus.BOOKED);
        long completedAppointmentsCount = appointmentRepository.countByStatus(AppointmentStatus.COMPLETED);
        long cancelledAppointmentsCount = appointmentRepository.countByStatus(AppointmentStatus.CANCELLED);

        return new AdminStatisticsResponse(
                servicesCount,
                employeesCount,
                appointmentsCount,
                bookedAppointmentsCount,
                completedAppointmentsCount,
                cancelledAppointmentsCount
        );
    }
}