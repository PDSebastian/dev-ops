package com.example.springbd3big.user.administrator.dtos;

public record AdministratorResponse(
        int id,
        String firstName,
        String lastName,
        String email,
        String employeeCode,
        String department,
        String jobTitle
) {
}
