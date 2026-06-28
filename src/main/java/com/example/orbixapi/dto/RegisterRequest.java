package com.example.orbixapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es válido")
        @JsonAlias({"correo", "correoElectronico"})
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        @JsonAlias({"contraseña", "contrasena"})
        String password,

        @JsonAlias({"nombreCompleto", "name"})
        String nombre,

        @JsonAlias({"fechaNacimiento", "birthDate"})
        LocalDate fechaNacimiento
) {
}
