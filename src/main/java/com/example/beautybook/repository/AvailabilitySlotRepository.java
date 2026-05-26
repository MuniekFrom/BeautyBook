package com.example.beautybook.repository;

import com.example.beautybook.model.AvailabilitySlot;
import com.example.beautybook.model.BeautyCategory;
import com.example.beautybook.model.enums.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findByStatus(SlotStatus status);

    List<AvailabilitySlot> findByEmployeeProfileId(Long employeeProfileId);

    List<AvailabilitySlot> findByEmployeeProfileIdAndStatus(Long employeeProfileId, SlotStatus status);

    List<AvailabilitySlot> findByStatusAndStartTimeAfter(
            SlotStatus status,
            LocalDateTime now
    );

    List<AvailabilitySlot> findByEmployeeProfileIdAndStartTimeAfter(
            Long employeeProfileId,
            LocalDateTime now
    );

    List<AvailabilitySlot> findByEmployeeProfileIdAndStatusAndStartTimeAfter(
            Long employeeProfileId,
            SlotStatus status,
            LocalDateTime now
    );

    List<AvailabilitySlot> findByEmployeeProfileCategoryAndStatusAndStartTimeAfter(
            BeautyCategory category,
            SlotStatus status,
            LocalDateTime now
    );

    boolean existsByEmployeeProfileIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long employeeProfileId,
            LocalDateTime endTime,
            LocalDateTime startTime
    );
}