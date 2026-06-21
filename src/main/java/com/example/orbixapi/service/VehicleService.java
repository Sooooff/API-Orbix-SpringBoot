package com.example.orbixapi.service;

import com.example.orbixapi.model.Vehicle;
import com.example.orbixapi.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository repository;

    public List<Vehicle> getAll(){
        return repository.findAll();
    }

    public Vehicle save(Vehicle vehicle){
        return repository.save(vehicle);
    }
}
