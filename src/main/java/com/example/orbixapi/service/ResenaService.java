package com.example.orbixapi.service;

import com.example.orbixapi.dto.CreateReviewBody;
import com.example.orbixapi.dto.CreateUserReviewRequest;
import com.example.orbixapi.dto.ReviewResponse;
import com.example.orbixapi.dto.UserReviewResponse;
import com.example.orbixapi.dto.AllReviewTagsResponse;
import com.example.orbixapi.dto.ReviewTagsResponse;
import com.example.orbixapi.dto.UserReviewSummary;
import com.example.orbixapi.dto.VehicleReviewSummary;
import com.example.orbixapi.model.Resena;
import com.example.orbixapi.model.ResenaUsuario;
import com.example.orbixapi.model.ReviewTagCatalog;
import com.example.orbixapi.model.ReviewType;
import com.example.orbixapi.model.RolNombre;
import com.example.orbixapi.model.Usuario;
import com.example.orbixapi.model.Vehicle;
import com.example.orbixapi.repository.ResenaRepository;
import com.example.orbixapi.repository.ResenaUsuarioRepository;
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
    private final ResenaUsuarioRepository resenaUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final VehicleRepository vehicleRepository;

    public ResenaService(
            ResenaRepository resenaRepository,
            ResenaUsuarioRepository resenaUsuarioRepository,
            UsuarioRepository usuarioRepository,
            VehicleRepository vehicleRepository
    ) {
        this.resenaRepository = resenaRepository;
        this.resenaUsuarioRepository = resenaUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public ReviewResponse create(Long vehiculoId, CreateReviewBody request, String reviewerEmail) {
        Usuario reviewer = usuarioRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + reviewerEmail));

        boolean isCliente = reviewer.getRoles().stream()
                .anyMatch(rol -> rol.getNombre() == RolNombre.ROLE_CLIENTE);
        if (!isCliente) {
            throw new IllegalArgumentException("Solo los clientes pueden reseñar vehículos");
        }

        Vehicle vehicle = vehicleRepository.findById(vehiculoId)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

        if (vehicle.getOwner() != null && vehicle.getOwner().getId().equals(reviewer.getId())) {
            throw new IllegalArgumentException("No puedes reseñar tu propio vehículo");
        }

        if (resenaRepository.findByReviewerIdAndVehicleId(reviewer.getId(), vehicle.getId()).isPresent()) {
            throw new IllegalArgumentException("Ya dejaste una reseña a este vehículo");
        }

        ReviewTagCatalog.validate(ReviewType.VEHICLE, request.rating(), request.tags());

        Resena resena = new Resena();
        resena.setReviewer(reviewer);
        resena.setVehicle(vehicle);
        resena.setRating(request.rating());
        resena.setTags(request.tags() != null ? new ArrayList<>(request.tags()) : new ArrayList<>());
        resena.setComment(request.comment());

        return toVehicleResponse(resenaRepository.save(resena));
    }

    @Transactional
    public UserReviewResponse createUserReview(CreateUserReviewRequest request, String reviewerEmail) {
        Usuario reviewer = usuarioRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + reviewerEmail));

        boolean isArrendador = reviewer.getRoles().stream()
                .anyMatch(rol -> rol.getNombre() == RolNombre.ROLE_ARRENDADOR);
        if (!isArrendador) {
            throw new IllegalArgumentException("Solo los arrendadores pueden reseñar clientes");
        }

        Usuario reviewed = usuarioRepository.findById(request.reviewedUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        boolean isCliente = reviewed.getRoles().stream()
                .anyMatch(rol -> rol.getNombre() == RolNombre.ROLE_CLIENTE);
        if (!isCliente) {
            throw new IllegalArgumentException("Solo se pueden reseñar clientes");
        }

        if (reviewer.getId().equals(reviewed.getId())) {
            throw new IllegalArgumentException("No puedes reseñarte a ti mismo");
        }

        if (resenaUsuarioRepository.findByReviewerIdAndReviewedId(reviewer.getId(), reviewed.getId()).isPresent()) {
            throw new IllegalArgumentException("Ya dejaste una reseña a este cliente");
        }

        ReviewTagCatalog.validate(ReviewType.USER, request.rating(), request.tags());

        ResenaUsuario resena = new ResenaUsuario();
        resena.setReviewer(reviewer);
        resena.setReviewed(reviewed);
        resena.setRating(request.rating());
        resena.setTags(request.tags() != null ? new ArrayList<>(request.tags()) : new ArrayList<>());
        resena.setComment(request.comment());

        return toUserResponse(resenaUsuarioRepository.save(resena));
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getByVehicle(Long vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new IllegalArgumentException("Vehículo no encontrado");
        }
        return resenaRepository.findByVehicleIdOrderByFechaDesc(vehicleId).stream()
                .map(this::toVehicleResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserReviewResponse> getByUser(Long userId) {
        if (!usuarioRepository.existsById(userId)) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        return resenaUsuarioRepository.findByReviewedIdOrderByFechaDesc(userId).stream()
                .map(this::toUserResponse)
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

    @Transactional(readOnly = true)
    public UserReviewSummary getUserSummary(Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        long total = resenaUsuarioRepository.countByReviewedId(userId);
        double average = total == 0 ? 0.0 : resenaUsuarioRepository.averageRatingByReviewedId(userId);

        return new UserReviewSummary(
                usuario.getId(),
                usuario.getNombre(),
                roundRating(average),
                total,
                sentimentLabel(average, total),
                usuario.getMemberSinceYear()
        );
    }

    public ReviewTagsResponse getTagsForRating(ReviewType type, int rating) {
        return new ReviewTagsResponse(
                rating,
                type,
                ReviewTagCatalog.optionsFor(type, rating)
        );
    }

    public AllReviewTagsResponse getAllTags() {
        return new AllReviewTagsResponse(
                ReviewTagCatalog.allVehicleOptions(),
                ReviewTagCatalog.allUserOptions()
        );
    }

    private ReviewResponse toVehicleResponse(Resena resena) {
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

    private UserReviewResponse toUserResponse(ResenaUsuario resena) {
        return new UserReviewResponse(
                resena.getId(),
                resena.getRating(),
                resena.getTags(),
                resena.getComment(),
                resena.getReviewer().getId(),
                resena.getReviewer().getNombre(),
                resena.getReviewed().getId(),
                resena.getReviewed().getNombre(),
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
