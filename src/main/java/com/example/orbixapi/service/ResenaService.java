package com.example.orbixapi.service;

import com.example.orbixapi.dto.CreateReviewRequest;
import com.example.orbixapi.dto.ReviewResponse;
import com.example.orbixapi.dto.VehicleReviewSummary;
import com.example.orbixapi.model.Resena;
import com.example.orbixapi.model.RolNombre;
import com.example.orbixapi.model.Usuario;
import com.example.orbixapi.model.Vehicle;
import com.example.orbixapi.repository.ResenaRepository;
import com.example.orbixapi.repository.UsuarioRepository;
import com.example.orbixapi.repository.VehicleRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VehicleRepository vehicleRepository;

    public ResenaService(
            ResenaRepository resenaRepository,
            UsuarioRepository usuarioRepository,
            VehicleRepository vehicleRepository
    ) {
        this.resenaRepository = resenaRepository;
        this.usuarioRepository = usuarioRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public ReviewResponse create(CreateReviewRequest request, String reviewerEmail) {
        Usuario reviewer = usuarioRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + reviewerEmail));

        boolean isCliente = reviewer.getRoles().stream()
                .anyMatch(rol -> rol.getNombre() == RolNombre.ROLE_CLIENTE);
        if (!isCliente) {
            throw new IllegalArgumentException("Solo los clientes pueden reseñar vehículos");
        }

        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

        if (vehicle.getOwner() != null && vehicle.getOwner().getId().equals(reviewer.getId())) {
            throw new IllegalArgumentException("No puedes reseñar tu propio vehículo");
        }

        if (resenaRepository.findByReviewerIdAndVehicleId(reviewer.getId(), vehicle.getId()).isPresent()) {
            throw new IllegalArgumentException("Ya dejaste una reseña a este vehículo");
        }

        Resena resena = new Resena();
        resena.setReviewer(reviewer);
        resena.setVehicle(vehicle);
        resena.setRating(request.rating());
        resena.setTags(request.tags() != null ? new ArrayList<>(request.tags()) : new ArrayList<>());
        resena.setComment(request.comment());

        return toResponse(resenaRepository.save(resena));
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getByVehicle(Long vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new IllegalArgumentException("Vehículo no encontrado");
        }
        return resenaRepository.findByVehicleIdOrderByFechaDesc(vehicleId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VehicleReviewSummary getVehicleSummary(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

        long total = resenaRepository.countByVehicleId(vehicleId);
        double average = total == 0 ? 0.0 : resenaRepository.averageRatingByVehicleId(vehicleId);

        String ownerName = vehicle.getOwner() != null ? vehicle.getOwner().getNombre() : null;

        return new VehicleReviewSummary(
                vehicle.getId(),
                vehicle.getBrand(),
                vehicle.getModel(),
                ownerName,
                roundRating(average),
                total,
                sentimentLabel(average, total)
        );
    }

    private ReviewResponse toResponse(Resena resena) {
        Vehicle vehicle = resena.getVehicle();
        return new ReviewResponse(
                resena.getId(),
                resena.getRating(),
                resena.getTags(),
                resena.getComment(),
                resena.getReviewer().getId(),
                resena.getReviewer().getNombre(),
                vehicle.getId(),
                vehicle.getBrand(),
                vehicle.getModel(),
                resena.getFecha()
        );
    }

    private double roundRating(double average) {
        return Math.round(average * 10.0) / 10.0;
    }

    private String sentimentLabel(double average, long total) {
        if (total == 0) {
            return "Sin reseñas";
        }
        if (average >= 4.5) {
            return "Reseñas Muy positivas";
        }
        if (average >= 3.5) {
            return "Reseñas Positivas";
        }
        if (average >= 2.5) {
            return "Reseñas Mixtas";
        }
        return "Reseñas Negativas";
    }
}
