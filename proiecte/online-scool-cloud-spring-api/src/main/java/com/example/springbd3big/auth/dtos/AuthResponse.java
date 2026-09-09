package com.example.springbd3big.auth.dtos;

import java.util.List;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long expiresInSeconds,
        UserMeResponse user
) {
}
