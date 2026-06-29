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
public class FavoriteService {

    private final UsuarioRepository usuarioRepository;
    private final VehicleRepository vehicleRepository;

    public FavoriteService(UsuarioRepository usuarioRepository, VehicleRepository vehicleRepository) {
        this.usuarioRepository = usuarioRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getFavorites(String email) {
        Usuario user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        return user.getFavoriteVehicles().stream()
                .map(v -> toResponse(v, true))
                .toList();
    }

    @Transactional
    public void addFavorite(String email, Long vehicleId) {
        Usuario user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado: " + vehicleId));

        user.getFavoriteVehicles().add(vehicle);
        usuarioRepository.save(user);
    }

    @Transactional
    public void removeFavorite(String email, Long vehicleId) {
        Usuario user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado: " + vehicleId));

        user.getFavoriteVehicles().remove(vehicle);
        usuarioRepository.save(user);
    }

    private VehicleResponse toResponse(Vehicle vehicle, boolean isFavorite) {
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
                owner != null ? owner.getNombre() : null,
                isFavorite
        );
    }
}
