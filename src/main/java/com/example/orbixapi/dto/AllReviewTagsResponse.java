package com.example.orbixapi.dto;

import java.util.List;
import java.util.Map;

public record AllReviewTagsResponse(
        Map<Integer, List<ReviewTagOption>> vehicle,
        Map<Integer, List<ReviewTagOption>> user,
        Map<Integer, String> vehicleTitles,
        Map<Integer, String> userTitles
) {
}
