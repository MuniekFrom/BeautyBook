package com.example.beautybook.dto;

import com.example.beautybook.model.enums.Role;

public class AdminUserResponse {

    private Long id;
    private String login;
    private Role role;
    private boolean active;

    public AdminUserResponse(Long id, String login, Role role, boolean active) {
        this.id = id;
        this.login = login;
        this.role = role;
        this.active = active;
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

    public boolean isActive() {
        return active;
    }
}