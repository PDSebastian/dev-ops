package com.example.springbd3big.user.permission.service;

import com.example.springbd3big.user.model.Permission;
import com.example.springbd3big.user.permission.dtos.PermissionResponse;
import com.example.springbd3big.user.permission.dtos.UserPermissionsResponse;

import java.util.List;
import java.util.Set;

public interface PermissionService {

    List<PermissionResponse> getAllPermissions();

    UserPermissionsResponse getUserPermissions(Long userId);

    UserPermissionsResponse addPermissions(Long userId, Set<Permission> permissions);

    UserPermissionsResponse removePermission(Long userId, Permission permission);
}
