package com.example.beautybook.service;

import com.example.beautybook.dto.CreateEmployeeProfileRequest;
import com.example.beautybook.dto.EmployeeProfileResponse;
import com.example.beautybook.exception.CategoryNotFoundException;
import com.example.beautybook.exception.EmployeeProfileAlreadyExistsException;
import com.example.beautybook.exception.EmployeeProfileNotFoundException;
import com.example.beautybook.exception.UserNotFoundException;
import com.example.beautybook.model.BeautyCategory;
import com.example.beautybook.model.EmployeeProfile;
import com.example.beautybook.model.User;
import com.example.beautybook.model.enums.Role;
import com.example.beautybook.repository.BeautyCategoryRepository;
import com.example.beautybook.repository.EmployeeProfileRepository;
import com.example.beautybook.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeProfileService {

    private final EmployeeProfileRepository employeeProfileRepository;
    private final UserRepository userRepository;
    private final BeautyCategoryRepository beautyCategoryRepository;

    public EmployeeProfileService(EmployeeProfileRepository employeeProfileRepository,
                                  UserRepository userRepository,
                                  BeautyCategoryRepository beautyCategoryRepository) {
        this.employeeProfileRepository = employeeProfileRepository;
        this.userRepository = userRepository;
        this.beautyCategoryRepository = beautyCategoryRepository;
    }

    public List<EmployeeProfileResponse> getAllEmployees() {
        return employeeProfileRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EmployeeProfileResponse getEmployeeById(Long id) {
        EmployeeProfile employeeProfile = employeeProfileRepository.findById(id)
                .orElseThrow(() -> new EmployeeProfileNotFoundException(
                        "Employee profile not found with id: " + id
                ));

        return mapToResponse(employeeProfile);
    }

    public EmployeeProfileResponse createEmployeeProfile(CreateEmployeeProfileRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + request.getUserId()
                ));

        if (user.getRole() != Role.EMPLOYEE) {
            throw new RuntimeException("User must have EMPLOYEE role");
        }

        if (employeeProfileRepository.existsByUserId(user.getId())) {
            throw new EmployeeProfileAlreadyExistsException(
                    "Employee profile already exists for user id: " + user.getId()
            );
        }

        BeautyCategory category = beautyCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category not found with id: " + request.getCategoryId()
                ));

        EmployeeProfile employeeProfile = new EmployeeProfile(
                request.getFirstName(),
                request.getLastName(),
                request.getSpecialization(),
                request.getDescription(),
                category,
                user
        );

        EmployeeProfile savedProfile = employeeProfileRepository.save(employeeProfile);

        return mapToResponse(savedProfile);
    }

    private EmployeeProfileResponse mapToResponse(EmployeeProfile employeeProfile) {

        User user = employeeProfile.getUser();
        BeautyCategory category = employeeProfile.getCategory();

        Long categoryId = category != null ? category.getId() : null;
        String categoryName = category != null ? category.getName() : null;

        return new EmployeeProfileResponse(
                employeeProfile.getId(),
                user.getId(),
                user.getLogin(),
                employeeProfile.getFirstName(),
                employeeProfile.getLastName(),
                employeeProfile.getSpecialization(),
                employeeProfile.getDescription(),
                categoryId,
                categoryName
        );
    }
}