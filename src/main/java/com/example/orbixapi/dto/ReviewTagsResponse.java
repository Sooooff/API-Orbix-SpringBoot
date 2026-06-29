package com.example.orbixapi.dto;

import com.example.orbixapi.model.ReviewType;

import java.util.List;

public record ReviewTagsResponse(
        int rating,
        ReviewType type,
        String title,
        List<ReviewTagOption> tags
) {
}
