package com.example.orbixapi.dto;

public record UserReviewSummary(
        Long userId,
        String nombre,
        double averageRating,
        long totalReviews,
        String sentimentLabel,
        Integer memberSinceYear
) {
}
