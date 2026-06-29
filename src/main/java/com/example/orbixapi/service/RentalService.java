package com.example.orbixapi.service;

import com.example.orbixapi.dto.CreateRentalRequest;
import com.example.orbixapi.dto.RentalResponse;
import com.example.orbixapi.model.Renta;
import com.example.orbixapi.model.RentalStatus;
import com.example.orbixapi.model.RolNombre;
import com.example.orbixapi.model.Usuario;
import com.example.orbixapi.model.Vehicle;
import com.example.orbixapi.repository.RentaRepository;
import com.example.orbixapi.repository.RentaRepository;
import com.example.orbixapi.repository.ResenaUsuarioRepository;
import com.example.orbixapi.repository.UsuarioRepository;
import com.example.orbixapi.repository.VehicleRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RentalService {

    private final RentaRepository rentaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VehicleRepository vehicleRepository;
    private final ResenaUsuarioRepository resenaUsuarioRepository;

    public RentalService(
            RentaRepository rentaRepository,
            UsuarioRepository usuarioRepository,
            VehicleRepository vehicleRepository,
            ResenaUsuarioRepository resenaUsuarioRepository
    ) {
        this.rentaRepository = rentaRepository;
        this.usuarioRepository = usuarioRepository;
        this.vehicleRepository = vehicleRepository;
        this.resenaUsuarioRepository = resenaUsuarioRepository;
    }

    @Transactional
    public RentalResponse create(CreateRentalRequest request, String clientEmail) {
        Usuario cliente = usuarioRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + clientEmail));

        boolean isCliente = cliente.getRoles().stream()
                .anyMatch(rol -> rol.getNombre() == RolNombre.ROLE_CLIENTE);
        if (!isCliente) {
            throw new IllegalArgumentException("Solo los clientes pueden solicitar rentas");
        }

        if (request.fechaFin().isBefore(request.fechaInicio())) {
            throw new IllegalArgumentException("La fecha fin debe ser igual o posterior a la fecha inicio");
        }

        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

        if (vehicle.getOwner() != null && vehicle.getOwner().getId().equals(cliente.getId())) {
            throw new IllegalArgumentException("No puedes rentar tu propio vehículo");
        }

        if (Boolean.FALSE.equals(vehicle.getAvailable())) {
            throw new IllegalArgumentException("El vehículo no está disponible");
        }

        if (rentaRepository.existsApprovedOverlap(
                vehicle.getId(), request.fechaInicio(), request.fechaFin())) {
            throw new IllegalArgumentException("El vehículo ya tiene una renta aprobada en esas fechas");
        }

        Renta renta = new Renta();
        renta.setVehicle(vehicle);
        renta.setCliente(cliente);
        renta.setFechaInicio(request.fechaInicio());
        renta.setFechaFin(request.fechaFin());
        renta.setEstado(RentalStatus.PENDIENTE);

        return toResponse(rentaRepository.save(renta));
    }

    @Transactional(readOnly = true)
    public List<RentalResponse> getMyRequests(String clientEmail) {
        Usuario cliente = usuarioRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + clientEmail));

        return rentaRepository.findByClienteIdOrderByFechaSolicitudDesc(cliente.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RentalResponse> getReceivedRequests(String ownerEmail) {
        Usuario owner = usuarioRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + ownerEmail));

        boolean isArrendador = owner.getRoles().stream()
                .anyMatch(rol -> rol.getNombre() == RolNombre.ROLE_ARRENDADOR
                        || rol.getNombre() == RolNombre.ROLE_ADMIN);
        if (!isArrendador) {
            throw new IllegalArgumentException("Solo los arrendadores pueden ver solicitudes recibidas");
        }

        return rentaRepository.findByVehicleOwnerIdOrderByFechaSolicitudDesc(owner.getId()).stream()
                .map(renta -> toResponse(renta, owner.getId()))
                .toList();
    }

    @Transactional
    public RentalResponse approve(Long rentalId, String ownerEmail) {
        return updateStatus(rentalId, ownerEmail, RentalStatus.APROBADA);
    }

    @Transactional
    public RentalResponse reject(Long rentalId, String ownerEmail) {
        return updateStatus(rentalId, ownerEmail, RentalStatus.RECHAZADA);
    }

    private RentalResponse updateStatus(Long rentalId, String ownerEmail, RentalStatus newStatus) {
        Usuario owner = usuarioRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + ownerEmail));

        Renta renta = rentaRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (renta.getVehicle().getOwner() == null
                || !renta.getVehicle().getOwner().getId().equals(owner.getId())) {
            throw new IllegalArgumentException("No puedes gestionar solicitudes de vehículos que no te pertenecen");
        }

        if (renta.getEstado() != RentalStatus.PENDIENTE) {
            throw new IllegalArgumentException("Esta solicitud ya fue procesada");
        }

        if (newStatus == RentalStatus.APROBADA
                && rentaRepository.existsApprovedOverlap(
                        renta.getVehicle().getId(),
                        renta.getFechaInicio(),
                        renta.getFechaFin())) {
            throw new IllegalArgumentException("El vehículo ya tiene una renta aprobada en esas fechas");
        }

        renta.setEstado(newStatus);
        return toResponse(rentaRepository.save(renta));
    }

    private RentalResponse toResponse(Renta renta) {
        return toResponse(renta, null);
    }

    private RentalResponse toResponse(Renta renta, Long ownerId) {
        Vehicle vehicle = renta.getVehicle();
        Usuario cliente = renta.getCliente();
        Usuario owner = vehicle.getOwner();

        long totalDias = ChronoUnit.DAYS.between(renta.getFechaInicio(), renta.getFechaFin()) + 1;
        double pricePerDay = vehicle.getPricePerDay() != null ? vehicle.getPricePerDay() : 0.0;
        double totalPrecio = totalDias * pricePerDay;

        boolean clienteAlreadyReviewed = ownerId != null
                && resenaUsuarioRepository.findByReviewerIdAndReviewedId(ownerId, cliente.getId()).isPresent();
        boolean canReviewCliente = ownerId != null
                && renta.getEstado() == RentalStatus.APROBADA
                && !clienteAlreadyReviewed;

        return new RentalResponse(
                renta.getId(),
                vehicle.getId(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getImageUrl(),
                cliente.getId(),
                cliente.getNombre(),
                cliente.getEmail(),
                cliente.getTelefono(),
                owner != null ? owner.getId() : null,
                owner != null ? owner.getNombre() : null,
                renta.getFechaInicio(),
                renta.getFechaFin(),
                renta.getEstado(),
                renta.getFechaSolicitud(),
                totalDias,
                totalPrecio,
                canReviewCliente,
                clienteAlreadyReviewed
        );
    }
}
