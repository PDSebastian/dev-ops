package com.example.springbd3big.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50) String firstName,
        @NotBlank @Size(min = 3, max = 50) String lastName,
        @Email @NotBlank @Size(min = 3, max = 50) String email,
        @NotBlank @Size(min = 8, max = 255) String password,
        String phoneNumber
) {
}
