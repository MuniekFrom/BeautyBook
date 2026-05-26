package com.example.beautybook.service;

import com.example.beautybook.dto.AvailabilitySlotResponse;
import com.example.beautybook.dto.CreateAvailabilitySlotRequest;
import com.example.beautybook.dto.CreateMyAvailabilitySlotRequest;
import com.example.beautybook.exception.EmployeeProfileNotFoundException;
import com.example.beautybook.exception.ServiceNotFoundException;
import com.example.beautybook.exception.SlotOverlapException;
import com.example.beautybook.model.AvailabilitySlot;
import com.example.beautybook.model.BeautyService;
import com.example.beautybook.model.EmployeeProfile;
import com.example.beautybook.model.enums.SlotStatus;
import com.example.beautybook.repository.AvailabilitySlotRepository;
import com.example.beautybook.repository.BeautyServiceRepository;
import com.example.beautybook.repository.EmployeeProfileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvailabilitySlotService {

    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final BeautyServiceRepository beautyServiceRepository;

    public AvailabilitySlotService(AvailabilitySlotRepository availabilitySlotRepository,
                                   EmployeeProfileRepository employeeProfileRepository,
                                   BeautyServiceRepository beautyServiceRepository) {
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.employeeProfileRepository = employeeProfileRepository;
        this.beautyServiceRepository = beautyServiceRepository;
    }

    public AvailabilitySlotResponse createSlot(CreateAvailabilitySlotRequest request) {

        EmployeeProfile employeeProfile = employeeProfileRepository.findById(request.getEmployeeProfileId())
                .orElseThrow(() -> new EmployeeProfileNotFoundException(
                        "Employee profile not found with id: " + request.getEmployeeProfileId()
                ));

        validateSlotTime(request.getStartTime(), request.getEndTime());
        validateSlotNotInPast(request.getStartTime());
        validateSlotOverlap(employeeProfile.getId(), request.getStartTime(), request.getEndTime());

        AvailabilitySlot slot = new AvailabilitySlot(
                request.getStartTime(),
                request.getEndTime(),
                SlotStatus.AVAILABLE,
                employeeProfile
        );

        AvailabilitySlot savedSlot = availabilitySlotRepository.save(slot);

        return mapToResponse(savedSlot);
    }

    public AvailabilitySlotResponse createMySlot(String employeeLogin, CreateMyAvailabilitySlotRequest request) {

        EmployeeProfile employeeProfile = employeeProfileRepository.findByUserLogin(employeeLogin)
                .orElseThrow(() -> new EmployeeProfileNotFoundException(
                        "Employee profile not found for login: " + employeeLogin
                ));

        if (request.getStartTime() == null) {
            throw new RuntimeException("Start time is required");
        }

        validateSlotNotInPast(request.getStartTime());

        /*
         * Techniczny endTime.
         * Pracownik wybiera tylko początek terminu.
         * Rzeczywisty czas wizyty wynika z wybranej usługi.
         */
        LocalDateTime technicalEndTime = request.getStartTime().plusMinutes(5);

        validateSlotOverlap(employeeProfile.getId(), request.getStartTime(), technicalEndTime);

        AvailabilitySlot slot = new AvailabilitySlot(
                request.getStartTime(),
                technicalEndTime,
                SlotStatus.AVAILABLE,
                employeeProfile
        );

        AvailabilitySlot savedSlot = availabilitySlotRepository.save(slot);

        return mapToResponse(savedSlot);
    }

    public List<AvailabilitySlotResponse> getAvailableSlots() {
        return availabilitySlotRepository.findByStatusAndStartTimeAfter(
                        SlotStatus.AVAILABLE,
                        LocalDateTime.now()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AvailabilitySlotResponse> getAvailableSlotsByService(Long serviceId) {

        BeautyService beautyService = beautyServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ServiceNotFoundException(
                        "Service not found with id: " + serviceId
                ));

        return availabilitySlotRepository
                .findByEmployeeProfileCategoryAndStatusAndStartTimeAfter(
                        beautyService.getCategory(),
                        SlotStatus.AVAILABLE,
                        LocalDateTime.now()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AvailabilitySlotResponse> getAvailableSlotsByEmployee(Long employeeProfileId) {
        return availabilitySlotRepository.findByEmployeeProfileIdAndStatusAndStartTimeAfter(
                        employeeProfileId,
                        SlotStatus.AVAILABLE,
                        LocalDateTime.now()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AvailabilitySlotResponse> getSlotsByEmployee(Long employeeProfileId) {
        return availabilitySlotRepository.findByEmployeeProfileIdAndStartTimeAfter(
                        employeeProfileId,
                        LocalDateTime.now()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AvailabilitySlotResponse> getMySlots(String employeeLogin) {

        EmployeeProfile employeeProfile = employeeProfileRepository.findByUserLogin(employeeLogin)
                .orElseThrow(() -> new EmployeeProfileNotFoundException(
                        "Employee profile not found for login: " + employeeLogin
                ));

        return availabilitySlotRepository.findByEmployeeProfileIdAndStartTimeAfter(
                        employeeProfile.getId(),
                        LocalDateTime.now()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validateSlotTime(LocalDateTime startTime, LocalDateTime endTime) {

        if (startTime == null || endTime == null) {
            throw new RuntimeException("Start time and end time are required");
        }

        if (!endTime.isAfter(startTime)) {
            throw new RuntimeException("End time must be after start time");
        }
    }

    private void validateSlotNotInPast(LocalDateTime startTime) {
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Start time cannot be in the past");
        }
    }

    private void validateSlotOverlap(Long employeeProfileId, LocalDateTime startTime, LocalDateTime endTime) {

        boolean overlaps = availabilitySlotRepository.existsByEmployeeProfileIdAndStartTimeLessThanAndEndTimeGreaterThan(
                employeeProfileId,
                endTime,
                startTime
        );

        if (overlaps) {
            throw new SlotOverlapException("This slot overlaps with another slot for this employee");
        }
    }

    private AvailabilitySlotResponse mapToResponse(AvailabilitySlot slot) {

        EmployeeProfile employee = slot.getEmployeeProfile();

        String employeeName = employee.getFirstName() + " " + employee.getLastName();

        return new AvailabilitySlotResponse(
                slot.getId(),
                employee.getId(),
                employeeName,
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getStatus()
        );
    }
}