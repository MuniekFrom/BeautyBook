package com.example.beautybook.exception;

public class EmployeeProfileNotFoundException extends RuntimeException {

    public EmployeeProfileNotFoundException(String message) {
        super(message);
    }
}