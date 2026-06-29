package com.example.orbixapi.dto;

import com.example.orbixapi.model.RentalStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExtensionResponse(
        Long id,
        Long rentaId,
        Integer diasExtension,
        RentalStatus estado,
        LocalDateTime fechaSolicitud,
        LocalDate nuevaFechaFin,
        Double costoAdicional,
        String vehicleBrand,
        String vehicleModel,
        String vehicleImageUrl,
        String clienteNombre,
        String clienteEmail
) {
}
