package com.example.springbd3big.user.permission.controller;

import com.example.springbd3big.user.permission.dtos.PermissionResponse;
import com.example.springbd3big.user.permission.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@Tag(name = "Permissions", description = "Permission catalog endpoints. Read operations require permission:read.")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @Operation(summary = "Get all permissions", description = "Required permission: permission:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<PermissionResponse> getAllPermissions() {
        return permissionService.getAllPermissions();
    }
}
