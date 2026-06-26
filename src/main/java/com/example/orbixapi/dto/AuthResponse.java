package com.example.orbixapi.dto;

import java.util.List;

public record AuthResponse(
        String token,
        String type,
        String email,
        List<String> roles
) {
    public static AuthResponse of(String token, String email, List<String> roles) {
        return new AuthResponse(token, "Bearer", email, roles);
    }
}
