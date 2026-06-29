package com.example.orbixapi.controller;

import com.example.orbixapi.dto.VehicleDto;
import com.example.orbixapi.dto.TransmissionOption;
import com.example.orbixapi.model.Transmission;
import com.example.orbixapi.model.Vehicle;
import com.example.orbixapi.service.VehicleService;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public List<Vehicle> getAll() {
        return service.getAll();
    }

    @GetMapping("/transmissions")
    public List<TransmissionOption> getTransmissions() {
        return Arrays.stream(Transmission.values())
                .map(t -> new TransmissionOption(t.name(), t.getLabel()))
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('vehicles:create')")
    public Vehicle save(
            @RequestBody Vehicle vehicle,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return service.save(vehicle, userDetails.getUsername());
    }
}
