package com.example.beautybook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterEmployeeRequest {

    @NotBlank(message = "Employee login is required")
    private String login;

    @NotBlank(message = "Employee password is required")
    @Size(min = 4, message = "Employee password must have at least 4 characters")
    private String password;

    public RegisterEmployeeRequest() {
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}