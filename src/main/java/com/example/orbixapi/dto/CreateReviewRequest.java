package com.example.orbixapi.dto;

import com.example.orbixapi.model.ReviewTag;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateReviewRequest(
        @NotNull @JsonProperty("vehiculoId") Long vehiculoId,
        @Min(1) @Max(5) int rating,
        List<ReviewTag> tags,
        @Size(max = 1000) String comment
) {
}
