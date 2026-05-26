package com.example.beautybook.dto;

public class BeautyCategoryResponse {

    private Long id;
    private String name;

    public BeautyCategoryResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}