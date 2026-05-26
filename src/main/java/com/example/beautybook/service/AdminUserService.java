package com.example.beautybook.service;

import com.example.beautybook.dto.AdminUserResponse;
import com.example.beautybook.exception.UserNotFoundException;
import com.example.beautybook.model.User;
import com.example.beautybook.model.enums.Role;
import com.example.beautybook.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> !user.isDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    public AdminUserResponse deactivateUser(Long userId) {
        User user = getUser(userId);

        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Admin account cannot be deactivated");
        }

        if (user.isDeleted()) {
            throw new RuntimeException("Deleted account cannot be deactivated");
        }

        user.setActive(false);
        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    public AdminUserResponse activateUser(Long userId) {
        User user = getUser(userId);

        if (user.isDeleted()) {
            throw new RuntimeException("Deleted account cannot be activated");
        }

        user.setActive(true);
        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    public void deleteInactiveUser(Long userId) {
        User user = getUser(userId);

        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Admin account cannot be deleted");
        }

        if (user.isActive()) {
            throw new RuntimeException("Account must be deactivated before deleting");
        }

        if (user.isDeleted()) {
            throw new RuntimeException("Account is already deleted");
        }

        String anonymizedLogin = "deleted_user_" + user.getId() + "_" + System.currentTimeMillis();

        user.setLogin(anonymizedLogin);
        user.setActive(false);
        user.setDeleted(true);

        userRepository.save(user);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    private AdminUserResponse mapToResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getLogin(),
                user.getRole(),
                user.isActive()
        );
    }
}