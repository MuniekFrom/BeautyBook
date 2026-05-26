package com.example.beautybook.service;

import com.example.beautybook.dto.LoginRequest;
import com.example.beautybook.dto.LoginResponse;
import com.example.beautybook.dto.MeResponse;
import com.example.beautybook.dto.RegisterEmployeeRequest;
import com.example.beautybook.dto.RegisterRequest;
import com.example.beautybook.dto.RegisterResponse;
import com.example.beautybook.dto.UserResponse;
import com.example.beautybook.exception.InvalidCredentialsException;
import com.example.beautybook.exception.UserAlreadyExistsException;
import com.example.beautybook.exception.UserNotFoundException;
import com.example.beautybook.model.User;
import com.example.beautybook.model.enums.Role;
import com.example.beautybook.repository.UserRepository;
import com.example.beautybook.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.beautybook.dto.ChangePasswordRequest;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public RegisterResponse registerClient(RegisterRequest request) {

        if (userRepository.existsByLogin(request.getLogin())) {
            throw new UserAlreadyExistsException("User with this login already exists");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getLogin(),
                encodedPassword,
                Role.CLIENT
        );

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getLogin(),
                savedUser.getRole(),
                "Client registered successfully"
        );
    }

    public UserResponse registerEmployee(RegisterEmployeeRequest request) {

        if (userRepository.existsByLogin(request.getLogin())) {
            throw new UserAlreadyExistsException("User with this login already exists");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User employee = new User(
                request.getLogin(),
                encodedPassword,
                Role.EMPLOYEE
        );

        User savedEmployee = userRepository.save(employee);

        return new UserResponse(
                savedEmployee.getId(),
                savedEmployee.getLogin(),
                savedEmployee.getRole(),
                "Employee account created successfully"
        );
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid login or password"));

        if (!user.isActive()) {
            throw new InvalidCredentialsException("Account is inactive");
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException("Invalid login or password");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                user.getId(),
                user.getLogin(),
                user.getRole(),
                token,
                "Login successful"
        );
    }

    public MeResponse getCurrentUser(String login) {

        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + login));

        return new MeResponse(
                user.getId(),
                user.getLogin(),
                user.getRole()
        );
    }

    public void changePassword(String login, ChangePasswordRequest request) {

        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + login));

        boolean currentPasswordMatches = passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        );

        if (!currentPasswordMatches) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());

        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }
}