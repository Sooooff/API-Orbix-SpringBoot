package com.example.orbixapi.dto;

import java.util.List;

public record AuthResponse(
        String token,
        String type,
        String email,
        List<String> roles,
        List<String> permissions
) {
    public static AuthResponse of(String token, String email, List<String> roles, List<String> permissions) {
        return new AuthResponse(token, "Bearer", email, roles, permissions);
    }
}
