package com.example.beautybook.dto;

import jakarta.validation.constraints.NotBlank;

public class BeautyCategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    public BeautyCategoryRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}