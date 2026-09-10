package com.example.springbd3big.auth.service;

import com.example.springbd3big.auth.dtos.AuthRequest;
import com.example.springbd3big.auth.dtos.AuthResponse;
import com.example.springbd3big.auth.dtos.RegisterRequest;
import com.example.springbd3big.auth.dtos.UserMeResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(AuthRequest request);

    UserMeResponse me();
}
