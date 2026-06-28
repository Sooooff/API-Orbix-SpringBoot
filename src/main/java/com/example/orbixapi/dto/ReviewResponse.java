package com.example.orbixapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.orbixapi.model.ReviewTag;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewResponse(
        Long id,
        int rating,
        List<ReviewTag> tags,
        String comment,
        Long reviewerId,
        String reviewerName,
        Long vehicleId,
        String vehicleBrand,
        String vehicleModel,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fecha
) {
}
