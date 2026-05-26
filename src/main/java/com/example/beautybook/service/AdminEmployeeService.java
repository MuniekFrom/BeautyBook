package com.example.beautybook.service;

import com.example.beautybook.dto.CreateEmployeeWithProfileRequest;
import com.example.beautybook.dto.EmployeeProfileResponse;
import com.example.beautybook.exception.CategoryNotFoundException;
import com.example.beautybook.exception.UserAlreadyExistsException;
import com.example.beautybook.model.BeautyCategory;
import com.example.beautybook.model.EmployeeProfile;
import com.example.beautybook.model.User;
import com.example.beautybook.model.enums.Role;
import com.example.beautybook.repository.BeautyCategoryRepository;
import com.example.beautybook.repository.EmployeeProfileRepository;
import com.example.beautybook.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminEmployeeService {

    private final UserRepository userRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final BeautyCategoryRepository beautyCategoryRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminEmployeeService(UserRepository userRepository,
                                EmployeeProfileRepository employeeProfileRepository,
                                BeautyCategoryRepository beautyCategoryRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.employeeProfileRepository = employeeProfileRepository;
        this.beautyCategoryRepository = beautyCategoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public EmployeeProfileResponse createEmployeeWithProfile(CreateEmployeeWithProfileRequest request) {

        if (userRepository.existsByLogin(request.getLogin())) {
            throw new UserAlreadyExistsException("User with this login already exists");
        }

        BeautyCategory category = beautyCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category not found with id: " + request.getCategoryId()
                ));

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getLogin(),
                encodedPassword,
                Role.EMPLOYEE
        );

        User savedUser = userRepository.save(user);

        EmployeeProfile employeeProfile = new EmployeeProfile(
                request.getFirstName(),
                request.getLastName(),
                request.getSpecialization(),
                request.getDescription(),
                category,
                savedUser
        );

        EmployeeProfile savedProfile = employeeProfileRepository.save(employeeProfile);

        return new EmployeeProfileResponse(
                savedProfile.getId(),
                savedUser.getId(),
                savedUser.getLogin(),
                savedProfile.getFirstName(),
                savedProfile.getLastName(),
                savedProfile.getSpecialization(),
                savedProfile.getDescription(),
                savedProfile.getCategory().getId(),
                savedProfile.getCategory().getName()
        );
    }
}