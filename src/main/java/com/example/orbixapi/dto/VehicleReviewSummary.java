package com.example.orbixapi.dto;

public record VehicleReviewSummary(
        Long vehicleId,
        String brand,
        String model,
        String ownerName,
        String ownerPhone,
        double averageRating,
        long totalReviews,
        String sentimentLabel
) {
}
