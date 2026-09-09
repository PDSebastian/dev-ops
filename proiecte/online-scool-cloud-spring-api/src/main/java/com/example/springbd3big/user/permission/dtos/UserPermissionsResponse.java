package com.example.springbd3big.user.permission.dtos;

import java.util.List;

public record UserPermissionsResponse(
        Long userId,
        List<String> permissions
) {
}
