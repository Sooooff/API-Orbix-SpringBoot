package com.example.orbixapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateExtensionRequest(
        @NotNull
        @Min(1)
        Integer diasExtension
) {
}
