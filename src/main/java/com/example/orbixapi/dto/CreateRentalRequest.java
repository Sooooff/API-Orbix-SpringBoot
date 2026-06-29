package com.example.orbixapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateRentalRequest(
        @NotNull Long vehicleId,
        @NotNull @FutureOrPresent @JsonAlias({"startDate", "fecha_inicio"})
        LocalDate fechaInicio,
        @NotNull @JsonAlias({"endDate", "fecha_fin"})
        LocalDate fechaFin
) {
}
