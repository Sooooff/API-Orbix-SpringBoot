package com.example.orbixapi.controller;

import com.example.orbixapi.dto.CreateReviewRequest;
import com.example.orbixapi.dto.ReviewResponse;
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
    public ReviewResponse create(
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return resenaService.create(request, userDetails.getUsername());
    }

    @GetMapping("/vehicle/{vehicleId}")
    public List<ReviewResponse> getByVehicle(@PathVariable Long vehicleId) {
        return resenaService.getByVehicle(vehicleId);
    }

    @GetMapping("/vehicle/{vehicleId}/summary")
    public VehicleReviewSummary getVehicleSummary(@PathVariable Long vehicleId) {
        return resenaService.getVehicleSummary(vehicleId);
    }
}
