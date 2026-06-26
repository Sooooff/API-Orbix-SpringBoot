package com.example.orbixapi.service;

import com.example.orbixapi.dto.AuthResponse;
import com.example.orbixapi.dto.LoginRequest;
import com.example.orbixapi.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
