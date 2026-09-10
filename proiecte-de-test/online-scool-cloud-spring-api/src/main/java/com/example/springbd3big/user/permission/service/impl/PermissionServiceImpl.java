package com.example.springbd3big.user.permission.service.impl;

import com.example.springbd3big.user.exceptions.UserNotFoundException;
import com.example.springbd3big.user.model.Permission;
import com.example.springbd3big.user.model.User;
import com.example.springbd3big.user.permission.dtos.PermissionResponse;
import com.example.springbd3big.user.permission.dtos.UserPermissionsResponse;
import com.example.springbd3big.user.permission.service.PermissionService;
import com.example.springbd3big.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final UserRepository userRepository;

    public PermissionServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        return List.of(Permission.values()).stream()
                .sorted(Comparator.comparing(Enum::name))
                .map(permission -> new PermissionResponse(permission.name(), permission.getAuthority()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserPermissionsResponse getUserPermissions(Long userId) {
        return toResponse(getUser(userId));
    }

    @Override
    @Transactional
    public UserPermissionsResponse addPermissions(Long userId, Set<Permission> permissions) {
        User user = getUser(userId);
        user.getPermissions().addAll(permissions);
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserPermissionsResponse removePermission(Long userId, Permission permission) {
        User user = getUser(userId);
        user.getPermissions().remove(permission);
        return toResponse(user);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private UserPermissionsResponse toResponse(User user) {
        return new UserPermissionsResponse(
                (long) user.getId(),
                user.getPermissions().stream()
                        .map(Permission::getAuthority)
                        .sorted()
                        .toList()
        );
    }
}
