package com.example.springbd3big.auth.service.impl;

import com.example.springbd3big.auth.dtos.AuthRequest;
import com.example.springbd3big.auth.dtos.AuthResponse;
import com.example.springbd3big.auth.dtos.RegisterRequest;
import com.example.springbd3big.auth.dtos.UserMeResponse;
import com.example.springbd3big.auth.service.AuthService;
import com.example.springbd3big.config.exceptions.InvalidRequestException;
import com.example.springbd3big.user.student.exceptions.StudentAlreadyExistsException;
import com.example.springbd3big.user.student.model.Student;
import com.example.springbd3big.user.model.Permission;
import com.example.springbd3big.user.model.PermissionTemplates;
import com.example.springbd3big.user.model.User;
import com.example.springbd3big.user.repository.UserRepository;
import com.example.springbd3big.config.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request == null) {
            throw new InvalidRequestException("request body must not be null");
        }

        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new StudentAlreadyExistsException(request.email());
        }

        User user = buildUser(request);
        user.setPermissions(PermissionTemplates.studentDefaults());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setActive(true);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);
        return buildAuthResponse(savedUser, token);
    }

    @Override
    @Transactional
    public AuthResponse login(AuthRequest request) {
        if (request == null) {
            throw new InvalidRequestException("request body must not be null");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = (User) authentication.getPrincipal();
        user.setLastLoginAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);
        return buildAuthResponse(savedUser, token);
    }

    @Override
    @Transactional(readOnly = true)
    public UserMeResponse me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new InvalidRequestException("authenticated user not found");
        }
        return toMeResponse(user);
    }

    private User buildUser(RegisterRequest request) {
        return Student.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .build();
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                toMeResponse(user)
        );
    }

    private UserMeResponse toMeResponse(User user) {
        List<String> permissions = user.getPermissions().stream()
                .map(Permission::getAuthority)
                .sorted(Comparator.naturalOrder())
                .toList();

        return new UserMeResponse(
                (long) user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.isActive(),
                permissions
        );
    }
}
