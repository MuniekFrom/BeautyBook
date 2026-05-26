package com.example.beautybook.exception;

public class EmployeeProfileAlreadyExistsException extends RuntimeException {

    public EmployeeProfileAlreadyExistsException(String message) {
        super(message);
    }
}