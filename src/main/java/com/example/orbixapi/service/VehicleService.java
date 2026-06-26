package com.example.orbixapi.service;

import com.example.orbixapi.model.Usuario;
import com.example.orbixapi.model.Vehicle;
import com.example.orbixapi.repository.UsuarioRepository;
import com.example.orbixapi.repository.VehicleRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository repository;
    private final UsuarioRepository usuarioRepository;

    public VehicleService(VehicleRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Vehicle> getAll() {
        return repository.findAll();
    }

    public Vehicle save(Vehicle vehicle, String ownerEmail) {
        Usuario owner = usuarioRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + ownerEmail));
        vehicle.setOwner(owner);
        return repository.save(vehicle);
    }
}
