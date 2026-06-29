package com.example.orbixapi.service;

import com.example.orbixapi.dto.VehicleResponse;
import com.example.orbixapi.model.Usuario;
import com.example.orbixapi.model.Vehicle;
import com.example.orbixapi.repository.UsuarioRepository;
import com.example.orbixapi.repository.VehicleRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository repository;
    private final UsuarioRepository usuarioRepository;

    public VehicleService(VehicleRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getAll() {
        return repository.findAllWithOwner().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getMine(String ownerEmail) {
        Usuario owner = usuarioRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + ownerEmail));

        return repository.findByOwnerIdWithOwner(owner.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VehicleResponse getById(Long id) {
        Vehicle vehicle = repository.findByIdWithOwner(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));
        return toResponse(vehicle);
    }

    @Transactional
    public VehicleResponse save(Vehicle vehicle, String ownerEmail) {
        Usuario owner = usuarioRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + ownerEmail));
        vehicle.setOwner(owner);
        if (vehicle.getAvailable() == null) {
            vehicle.setAvailable(true);
        }
        if (vehicle.getCategory() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }
        if (vehicle.getTransmission() == null) {
            throw new IllegalArgumentException("La transmisión es obligatoria");
        }
        return toResponse(repository.save(vehicle));
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        Usuario owner = vehicle.getOwner();
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getTransmission(),
                vehicle.getPassengers(),
                vehicle.getPricePerDay(),
                vehicle.getImageUrl(),
                vehicle.getAvailable(),
                vehicle.getDescription(),
                vehicle.getCategory(),
                owner != null ? owner.getId() : null,
                owner != null ? owner.getNombre() : null
        );
    }
}
