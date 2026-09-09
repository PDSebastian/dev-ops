package com.example.springbd3big.user.teacher.exceptions;

public class TeacherNotFoundException extends RuntimeException {

    public TeacherNotFoundException(Long teacherId) {
        super("teacher with id " + teacherId + " not found");
    }

    public TeacherNotFoundException(String email) {
        super("teacher with email " + email + " not found");
    }
}
