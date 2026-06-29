package com.example.orbixapi.controller;

import com.example.orbixapi.dto.CreateRentalRequest;
import com.example.orbixapi.dto.RentalResponse;
import com.example.orbixapi.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponse create(
            @Valid @RequestBody CreateRentalRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return rentalService.create(request, userDetails.getUsername());
    }

    @GetMapping("/mine")
    public List<RentalResponse> myRequests(@AuthenticationPrincipal UserDetails userDetails) {
        return rentalService.getMyRequests(userDetails.getUsername());
    }

    @GetMapping("/received")
    public List<RentalResponse> receivedRequests(@AuthenticationPrincipal UserDetails userDetails) {
        return rentalService.getReceivedRequests(userDetails.getUsername());
    }

    @PatchMapping("/{id}/approve")
    public RentalResponse approve(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return rentalService.approve(id, userDetails.getUsername());
    }

    @PatchMapping("/{id}/reject")
    public RentalResponse reject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return rentalService.reject(id, userDetails.getUsername());
    }
}
