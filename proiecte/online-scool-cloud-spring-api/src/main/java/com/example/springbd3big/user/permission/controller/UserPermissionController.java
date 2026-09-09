package com.example.springbd3big.user.permission.controller;

import com.example.springbd3big.user.model.Permission;
import com.example.springbd3big.user.permission.dtos.UpdateUserPermissionsRequest;
import com.example.springbd3big.user.permission.dtos.UserPermissionsResponse;
import com.example.springbd3big.user.permission.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/users/{userId}/permissions")
@Tag(name = "User Permissions", description = "User permission management endpoints. Read operations require user:read and write operations require user:write.")
public class UserPermissionController {

    private final PermissionService permissionService;

    public UserPermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @Operation(summary = "Get permissions for user", description = "Required permission: user:read", security = @SecurityRequirement(name = "bearerAuth"))
    public UserPermissionsResponse getUserPermissions(@PathVariable @Positive Long userId) {
        return permissionService.getUserPermissions(userId);
    }

    @PostMapping
    @Operation(summary = "Add permissions to user", description = "Required permission: user:write", security = @SecurityRequirement(name = "bearerAuth"))
    public UserPermissionsResponse addPermissions(
            @PathVariable @Positive Long userId,
            @Valid @RequestBody UpdateUserPermissionsRequest request
    ) {
        return permissionService.addPermissions(userId, request.permissions());
    }

    @DeleteMapping("/{permission}")
    @Operation(summary = "Remove permission from user", description = "Required permission: user:write", security = @SecurityRequirement(name = "bearerAuth"))
    public UserPermissionsResponse removePermission(
            @PathVariable @Positive Long userId,
            @PathVariable Permission permission
    ) {
        return permissionService.removePermission(userId, permission);
    }
}
