package com.example.beautybook.dto;

import com.example.beautybook.model.enums.Role;

public class MeResponse {

    private Long id;
    private String login;
    private Role role;

    public MeResponse(Long id, String login, Role role) {
        this.id = id;
        this.login = login;
        this.role = role;
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
}