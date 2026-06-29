package com.example.orbixapi.dto;

import java.util.List;

public record ClientProfileResponse(
        Long userId,
        String nombre,
        String email,
        Integer memberSinceYear,
        UserReviewSummary reviewSummary,
        List<UserReviewResponse> reviews
) {
}
