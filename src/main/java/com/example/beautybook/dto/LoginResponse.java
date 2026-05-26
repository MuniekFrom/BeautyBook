package com.example.beautybook.dto;

import com.example.beautybook.model.enums.Role;

public class LoginResponse {

    private Long id;
    private String login;
    private Role role;
    private String token;
    private String message;

    public LoginResponse(Long id, String login, Role role, String token, String message) {
        this.id = id;
        this.login = login;
        this.role = role;
        this.token = token;
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

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}