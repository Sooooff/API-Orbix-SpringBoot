package com.example.orbixapi.dto;

import com.example.orbixapi.model.ReviewTag;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateUserReviewRequest(
        @NotNull Long reviewedUserId,
        @Min(1) @Max(5) int rating,
        List<ReviewTag> tags,
        @Size(max = 1000) String comment
) {
}
