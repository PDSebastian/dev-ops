package com.example.springbd3big.auth.dtos;

import java.util.List;

public record UserMeResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        boolean active,
        List<String> permissions
) {
}
