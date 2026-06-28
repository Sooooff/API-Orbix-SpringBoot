package com.example.orbixapi.dto;

public record VehicleReviewSummary(
        Long vehicleId,
        String brand,
        String model,
        String ownerName,
        double averageRating,
        long totalReviews,
        String sentimentLabel
) {
}
