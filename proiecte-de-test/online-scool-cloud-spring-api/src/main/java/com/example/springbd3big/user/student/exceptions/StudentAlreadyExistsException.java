package com.example.springbd3big.user.student.exceptions;

public class StudentAlreadyExistsException extends RuntimeException {
    public StudentAlreadyExistsException(String email) {
        super("Student already exists with email=" + email);
    }
}
