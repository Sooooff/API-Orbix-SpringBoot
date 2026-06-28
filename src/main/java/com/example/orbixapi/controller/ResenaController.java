package com.example.orbixapi.controller;

import com.example.orbixapi.dto.CreateReviewRequest;
import com.example.orbixapi.dto.CreateUserReviewRequest;
import com.example.orbixapi.dto.ReviewResponse;
import com.example.orbixapi.dto.AllReviewTagsResponse;
import com.example.orbixapi.dto.ReviewTagsResponse;
import com.example.orbixapi.dto.UserReviewResponse;
import com.example.orbixapi.model.ReviewType;
import com.example.orbixapi.dto.UserReviewSummary;
import com.example.orbixapi.dto.VehicleReviewSummary;
import com.example.orbixapi.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse createVehicleReview(
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return resenaService.create(request, userDetails.getUsername());
    }

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserReviewResponse createUserReview(
            @Valid @RequestBody CreateUserReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return resenaService.createUserReview(request, userDetails.getUsername());
    }

    @GetMapping("/tags/all")
    public AllReviewTagsResponse getAllTags() {
        return resenaService.getAllTags();
    }

    @GetMapping("/tags")
    public ReviewTagsResponse getTags(
            @RequestParam int rating,
            @RequestParam ReviewType type
    ) {
        return resenaService.getTagsForRating(type, rating);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public List<ReviewResponse> getByVehicle(@PathVariable Long vehicleId) {
        return resenaService.getByVehicle(vehicleId);
    }

    @GetMapping("/vehicle/{vehicleId}/summary")
    public VehicleReviewSummary getVehicleSummary(@PathVariable Long vehicleId) {
        return resenaService.getVehicleSummary(vehicleId);
    }

    @GetMapping("/user/{userId}")
    public List<UserReviewResponse> getByUser(@PathVariable Long userId) {
        return resenaService.getByUser(userId);
    }

    @GetMapping("/user/{userId}/summary")
    public UserReviewSummary getUserSummary(@PathVariable Long userId) {
        return resenaService.getUserSummary(userId);
    }
}
