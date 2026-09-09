package com.example.springbd3big.auth.controller;

import com.example.springbd3big.auth.dtos.AuthRequest;
import com.example.springbd3big.auth.dtos.AuthResponse;
import com.example.springbd3big.auth.dtos.RegisterRequest;
import com.example.springbd3big.auth.dtos.UserMeResponse;
import com.example.springbd3big.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication and current user endpoints. Bootstrap accounts are documented in the Swagger API description.")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Register a new user",
            description = "Public endpoint. Creates a student account with the default student permission set and returns a JWT token. No permission required."
    )
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate and receive JWT",
            description = "Public endpoint. Use one of the bootstrap users shown in the API description. Default password for initialized users: password."
    )
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get current authenticated user",
            description = "Protected endpoint. Requires a valid Bearer JWT token. Accessible by any authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public UserMeResponse me() {
        return authService.me();
    }
}
