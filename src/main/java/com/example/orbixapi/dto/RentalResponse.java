package com.example.orbixapi.dto;

import com.example.orbixapi.model.RentalStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RentalResponse(
        Long id,
        Long vehicleId,
        String vehicleBrand,
        String vehicleModel,
        String vehicleImageUrl,
        Long clienteId,
        String clienteNombre,
        String clienteEmail,
        Long ownerId,
        String ownerNombre,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate fechaInicio,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate fechaFin,
        RentalStatus estado,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fechaSolicitud,
        long totalDias,
        double totalPrecio,
        boolean canReviewCliente,
        boolean clienteAlreadyReviewed
) {
}
