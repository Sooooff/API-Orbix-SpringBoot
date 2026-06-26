package com.example.orbixapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VehicleDto(
        Long id,
        @NotBlank String brand,
        @NotBlank String model,
        @NotBlank String year,
        @NotBlank String transmission,
        @NotBlank String passengers,
        @NotNull @Positive Double pricePerDay,
        String imageUrl,
        @NotNull Boolean available
) {
}
