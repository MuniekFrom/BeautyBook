package com.example.beautybook.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employee_profiles")
public class EmployeeProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String specialization;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private BeautyCategory category;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public EmployeeProfile() {
    }

    public EmployeeProfile(String firstName,
                           String lastName,
                           String specialization,
                           String description,
                           BeautyCategory category,
                           User user) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.description = description;
        this.category = category;
        this.user = user;
    }

    public EmployeeProfile(String firstName,
                           String lastName,
                           String specialization,
                           String description,
                           User user) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.description = description;
        this.user = user;
    }

    public Long getId() {
        return id;
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

    public BeautyCategory getCategory() {
        return category;
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(BeautyCategory category) {
        this.category = category;
    }

    public void setUser(User user) {
        this.user = user;
    }
}