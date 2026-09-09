package com.example.springbd3big.user.teacher.dtos;

public record TeacherResponse(
        int id,
        String firstName,
        String lastName,
        String email,
        String employeeCode,
        String specialization,
        String title
) {
}
