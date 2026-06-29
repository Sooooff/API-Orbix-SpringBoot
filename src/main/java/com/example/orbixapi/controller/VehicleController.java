package com.example.orbixapi.controller;

import com.example.orbixapi.dto.TransmissionOption;
import com.example.orbixapi.dto.VehicleResponse;
import com.example.orbixapi.model.Transmission;
import com.example.orbixapi.model.Vehicle;
import com.example.orbixapi.service.VehicleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @GetMapping
    public List<VehicleResponse> getAll(@AuthenticationPrincipal UserDetails userDetails) {
        if (isArrendador(userDetails)) {
            return service.getMine(userDetails.getUsername());
        }
        return service.getAll(userDetails != null ? userDetails.getUsername() : null);
    }

    private boolean isArrendador(UserDetails userDetails) {
        if (userDetails == null) {
            return false;
        }
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ARRENDADOR"::equals);
    }

    @GetMapping("/mine")
    public List<VehicleResponse> getMine(@AuthenticationPrincipal UserDetails userDetails) {
        return service.getMine(userDetails.getUsername());
    }

    @GetMapping("/transmissions")
    public List<TransmissionOption> getTransmissions() {
        return Arrays.stream(Transmission.values())
                .map(t -> new TransmissionOption(t.name(), t.getLabel()))
                .toList();
    }

    @GetMapping("/{id}")
    public VehicleResponse getById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return service.getById(id, userDetails != null ? userDetails.getUsername() : null);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('vehicles:create')")
    public VehicleResponse save(
            @RequestBody Vehicle vehicle,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return service.save(vehicle, userDetails.getUsername());
    }
}
