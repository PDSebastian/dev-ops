package com.example.springbd3big.user.student.exceptions;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(Long studentId) {
        super(studentId == null
                ? "Student not found"
                : "Student not found with id=" + studentId);
    }

    public StudentNotFoundException(String email) {
        super("Student not found with email=" + email);
    }
}
