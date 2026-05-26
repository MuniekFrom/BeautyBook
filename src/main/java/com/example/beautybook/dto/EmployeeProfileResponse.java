package com.example.beautybook.dto;

public class EmployeeProfileResponse {

    private Long id;
    private Long userId;
    private String login;
    private String firstName;
    private String lastName;
    private String specialization;
    private String description;
    private Long categoryId;
    private String categoryName;

    public EmployeeProfileResponse(Long id,
                                   Long userId,
                                   String login,
                                   String firstName,
                                   String lastName,
                                   String specialization,
                                   String description,
                                   Long categoryId,
                                   String categoryName) {
        this.id = id;
        this.userId = userId;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.description = description;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getDescription() {
        return description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }
}