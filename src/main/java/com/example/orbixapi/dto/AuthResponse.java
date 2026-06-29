package com.example.orbixapi.dto;

import java.util.List;

public record AuthResponse(
        String token,
        String type,
        String email,
        List<String> roles,
        List<String> permissions,
        Long userId,
        String nombre,
        String telefono
) {
    public static AuthResponse of(
            String token,
            String email,
            Long userId,
            String nombre,
            String telefono,
            List<String> roles,
            List<String> permissions
    ) {
        return new AuthResponse(token, "Bearer", email, roles, permissions, userId, nombre, telefono);
    }
}
