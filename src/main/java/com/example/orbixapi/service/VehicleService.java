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
import java.util.Set;
import java.util.HashSet;

@Service
public class VehicleService {

    private final VehicleRepository repository;
    private final UsuarioRepository usuarioRepository;

    public VehicleService(VehicleRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getAll(String userEmail) {
        Set<Long> favoriteVehicleIds = getFavoriteVehicleIds(userEmail);
        return repository.findAllWithOwner().stream()
                .map(v -> toResponse(v, favoriteVehicleIds))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getMine(String ownerEmail) {
        Usuario owner = usuarioRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + ownerEmail));

        return repository.findByOwnerIdWithOwner(owner.getId()).stream()
                .map(v -> toResponse(v, Set.of()))
                .toList();
    }

    @Transactional(readOnly = true)
    public VehicleResponse getById(Long id, String userEmail) {
        Vehicle vehicle = repository.findByIdWithOwner(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));
        Set<Long> favoriteVehicleIds = getFavoriteVehicleIds(userEmail);
        return toResponse(vehicle, favoriteVehicleIds);
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
        return toResponse(repository.save(vehicle), Set.of());
    }

    private Set<Long> getFavoriteVehicleIds(String userEmail) {
        Set<Long> favoriteVehicleIds = new HashSet<>();
        if (userEmail != null) {
            usuarioRepository.findByEmail(userEmail).ifPresent(user -> {
                user.getFavoriteVehicles().forEach(fav -> favoriteVehicleIds.add(fav.getId()));
            });
        }
        return favoriteVehicleIds;
    }

    private VehicleResponse toResponse(Vehicle vehicle, Set<Long> favoriteVehicleIds) {
        Usuario owner = vehicle.getOwner();
        boolean isFavorite = favoriteVehicleIds.contains(vehicle.getId());
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
