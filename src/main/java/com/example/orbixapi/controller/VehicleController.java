package com.example.orbixapi.controller;

import com.example.orbixapi.model.Vehicle;
import com.example.orbixapi.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@CrossOrigin("*")
public class VehicleController {
    @Autowired
    private VehicleService service;

    @GetMapping
    public List<Vehicle> getAll(){
        return service.getAll();
    }

    @PostMapping
    public Vehicle save(
            @RequestBody Vehicle vehicle){
        return service.save(vehicle);
    }
}
