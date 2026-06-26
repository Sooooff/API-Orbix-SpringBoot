package com.example.orbixapi.controller;

import com.example.orbixapi.dto.VehicleDto;
import com.example.orbixapi.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@CrossOrigin("*")
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @GetMapping
    public List<VehicleDto> getAll() {
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleDto save(@Valid @RequestBody VehicleDto dto) {
        return service.save(dto);
    }
}
