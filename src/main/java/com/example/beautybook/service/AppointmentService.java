package com.example.beautybook.service;

import com.example.beautybook.dto.AppointmentResponse;
import com.example.beautybook.dto.BookAppointmentRequest;
import com.example.beautybook.exception.*;
import com.example.beautybook.model.*;
import com.example.beautybook.model.enums.AppointmentStatus;
import com.example.beautybook.model.enums.SlotStatus;
import com.example.beautybook.repository.*;
import org.springframework.stereotype.Service;
import com.example.beautybook.exception.EmployeeProfileNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final BeautyServiceRepository beautyServiceRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final EmployeeProfileRepository employeeProfileRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              UserRepository userRepository,
                              BeautyServiceRepository beautyServiceRepository,
                              AvailabilitySlotRepository availabilitySlotRepository,
                              EmployeeProfileRepository employeeProfileRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.beautyServiceRepository = beautyServiceRepository;
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.employeeProfileRepository = employeeProfileRepository;
    }

    public AppointmentResponse bookAppointment(String clientLogin, BookAppointmentRequest request) {

        User client = userRepository.findByLogin(clientLogin)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + clientLogin));

        BeautyService beautyService = beautyServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ServiceNotFoundException("Service not found with id: " + request.getServiceId()));

        AvailabilitySlot slot = availabilitySlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with id: " + request.getSlotId()));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new SlotNotAvailableException("This slot is not available");
        }

        boolean alreadyBooked = appointmentRepository.existsByAvailabilitySlotIdAndStatus(
                slot.getId(),
                AppointmentStatus.BOOKED
        );

        if (alreadyBooked) {
            throw new SlotNotAvailableException("This slot is already booked");
        }

        Appointment appointment = new Appointment(
                client,
                beautyService,
                slot,
                AppointmentStatus.BOOKED,
                request.getNotes(),
                LocalDateTime.now()
        );

        slot.setStatus(SlotStatus.BOOKED);
        availabilitySlotRepository.save(slot);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return mapToResponse(savedAppointment);
    }

    public List<AppointmentResponse> getMyAppointments(String clientLogin) {
        return appointmentRepository.findByClientLogin(clientLogin)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {

        EmployeeProfile employee = appointment.getAvailabilitySlot().getEmployeeProfile();
        String employeeName = employee.getFirstName() + " " + employee.getLastName();

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getClient().getLogin(),
                appointment.getBeautyService().getName(),
                employeeName,
                appointment.getAvailabilitySlot().getStartTime(),
                appointment.getAvailabilitySlot().getEndTime(),
                appointment.getStatus(),
                appointment.getNotes()
        );
    }

    public AppointmentResponse cancelMyAppointment(String clientLogin, Long appointmentId) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        if (!appointment.getClient().getLogin().equals(clientLogin)) {
            throw new RuntimeException("You cannot cancel someone else's appointment");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment is already cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        AvailabilitySlot slot = appointment.getAvailabilitySlot();
        slot.setStatus(SlotStatus.AVAILABLE);

        availabilitySlotRepository.save(slot);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return mapToResponse(savedAppointment);
    }

    public List<AppointmentResponse> getMyEmployeeAppointments(String employeeLogin) {

        EmployeeProfile employeeProfile = employeeProfileRepository.findByUserLogin(employeeLogin)
                .orElseThrow(() -> new EmployeeProfileNotFoundException(
                        "Employee profile not found for login: " + employeeLogin
                ));

        return appointmentRepository.findByAvailabilitySlotEmployeeProfileId(employeeProfile.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AppointmentResponse completeAppointment(String employeeLogin, Long appointmentId) {

        EmployeeProfile employeeProfile = employeeProfileRepository.findByUserLogin(employeeLogin)
                .orElseThrow(() -> new EmployeeProfileNotFoundException(
                        "Employee profile not found for login: " + employeeLogin
                ));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        Long appointmentEmployeeId = appointment
                .getAvailabilitySlot()
                .getEmployeeProfile()
                .getId();

        if (!appointmentEmployeeId.equals(employeeProfile.getId())) {
            throw new RuntimeException("You cannot complete someone else's appointment");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Cannot complete cancelled appointment");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return mapToResponse(savedAppointment);
    }
}