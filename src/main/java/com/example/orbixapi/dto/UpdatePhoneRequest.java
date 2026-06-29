package com.example.orbixapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePhoneRequest(
        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(
                regexp = "^[+]?[0-9\\s-]{8,15}$",
                message = "El teléfono debe tener entre 8 y 15 dígitos"
        )
        @JsonAlias({"telefono", "phone", "celular"})
        String telefono
) {
}
