package com.example.springbd3big.user.permission.dtos;

import com.example.springbd3big.user.model.Permission;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UpdateUserPermissionsRequest(
        @NotEmpty Set<Permission> permissions
) {
}
