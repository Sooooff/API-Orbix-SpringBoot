package com.example.orbixapi.dto;

import com.example.orbixapi.model.Transmission;
import com.example.orbixapi.model.VehicleCategory;

public record VehicleResponse(
        Long id,
        String brand,
        String model,
        String year,
        Transmission transmission,
        String passengers,
        Double pricePerDay,
        String imageUrl,
        Boolean available,
        String description,
        VehicleCategory category,
        Long ownerId,
        String ownerName
) {
}
