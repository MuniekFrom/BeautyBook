package com.example.beautybook.dto;

import com.example.beautybook.model.enums.Role;

public class RegisterResponse {

    private Long id;
    private String login;
    private Role role;
    private String message;

    public RegisterResponse(Long id, String login, Role role, String message) {
        this.id = id;
        this.login = login;
        this.role = role;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public Role getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }
}