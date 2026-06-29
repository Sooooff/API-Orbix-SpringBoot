package com.example.orbixapi.controller;

import com.example.orbixapi.dto.VehicleResponse;
import com.example.orbixapi.service.FavoriteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public List<VehicleResponse> getFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        return favoriteService.getFavorites(userDetails.getUsername());
    }

    @PostMapping("/{vehicleId}")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public void addFavorite(
            @PathVariable Long vehicleId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        favoriteService.addFavorite(userDetails.getUsername(), vehicleId);
    }

    @DeleteMapping("/{vehicleId}")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public void removeFavorite(
            @PathVariable Long vehicleId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        favoriteService.removeFavorite(userDetails.getUsername(), vehicleId);
    }
}
