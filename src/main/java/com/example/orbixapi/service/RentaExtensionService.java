package com.example.orbixapi.service;

import com.example.orbixapi.dto.CreateExtensionRequest;
import com.example.orbixapi.dto.ExtensionResponse;
import com.example.orbixapi.model.Renta;
import com.example.orbixapi.model.RentaExtension;
import com.example.orbixapi.model.RentalStatus;
import com.example.orbixapi.model.RolNombre;
import com.example.orbixapi.model.Usuario;
import com.example.orbixapi.model.Vehicle;
import com.example.orbixapi.repository.RentaExtensionRepository;
import com.example.orbixapi.repository.RentaRepository;
import com.example.orbixapi.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RentaExtensionService {

    private final RentaExtensionRepository rentaExtensionRepository;
    private final RentaRepository rentaRepository;
    private final UsuarioRepository usuarioRepository;

    public RentaExtensionService(
            RentaExtensionRepository rentaExtensionRepository,
            RentaRepository rentaRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.rentaExtensionRepository = rentaExtensionRepository;
        this.rentaRepository = rentaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ExtensionResponse createExtensionRequest(Long rentalId, CreateExtensionRequest request, String clientEmail) {
        Usuario cliente = usuarioRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + clientEmail));

        boolean isCliente = cliente.getRoles().stream()
                .anyMatch(rol -> rol.getNombre() == RolNombre.ROLE_CLIENTE);
        if (!isCliente) {
            throw new IllegalArgumentException("Solo los clientes pueden solicitar extensiones de renta");
        }

        Renta renta = rentaRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Renta no encontrada"));

        if (!renta.getCliente().getId().equals(cliente.getId())) {
            throw new IllegalArgumentException("No puedes solicitar extensiones para rentas que no te pertenecen");
        }

        if (renta.getEstado() != RentalStatus.APROBADA) {
            throw new IllegalArgumentException("Solo se pueden solicitar extensiones para rentas aprobadas");
        }

        // Check if there is already a pending extension request for this rental
        List<RentaExtension> extensions = rentaExtensionRepository.findByRentaIdOrderByFechaSolicitudDesc(rentalId);
        boolean hasPending = extensions.stream().anyMatch(e -> e.getEstado() == RentalStatus.PENDIENTE);
        if (hasPending) {
            throw new IllegalArgumentException("Ya existe una solicitud de extensión pendiente para esta renta");
        }

        LocalDate currentFechaFin = renta.getFechaFin();
        LocalDate nuevaFechaFin = currentFechaFin.plusDays(request.diasExtension());

        // Check for overlap during the extension period
        if (rentaRepository.existsApprovedOverlap(renta.getVehicle().getId(), currentFechaFin.plusDays(1), nuevaFechaFin)) {
            throw new IllegalArgumentException("El vehículo ya tiene una renta aprobada durante el período de extensión solicitado");
        }

        RentaExtension extension = new RentaExtension();
        extension.setRenta(renta);
        extension.setDiasExtension(request.diasExtension());
        extension.setFechaFinNueva(nuevaFechaFin);
        extension.setEstado(RentalStatus.PENDIENTE);

        return toResponse(rentaExtensionRepository.save(extension));
    }

    @Transactional
    public ExtensionResponse approveExtension(Long extensionId, String ownerEmail) {
        return updateExtensionStatus(extensionId, ownerEmail, RentalStatus.APROBADA);
    }

    @Transactional
    public ExtensionResponse rejectExtension(Long extensionId, String ownerEmail) {
        return updateExtensionStatus(extensionId, ownerEmail, RentalStatus.RECHAZADA);
    }

    private ExtensionResponse updateExtensionStatus(Long extensionId, String ownerEmail, RentalStatus newStatus) {
        Usuario owner = usuarioRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + ownerEmail));

        RentaExtension extension = rentaExtensionRepository.findById(extensionId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud de extensión no encontrada"));

        Renta renta = extension.getRenta();
        Vehicle vehicle = renta.getVehicle();

        if (vehicle.getOwner() == null || !vehicle.getOwner().getId().equals(owner.getId())) {
            throw new IllegalArgumentException("No puedes gestionar solicitudes de extensión de vehículos que no te pertenecen");
        }

        if (extension.getEstado() != RentalStatus.PENDIENTE) {
            throw new IllegalArgumentException("Esta solicitud de extensión ya fue procesada");
        }

        if (newStatus == RentalStatus.APROBADA) {
            LocalDate currentFechaFin = renta.getFechaFin();
            LocalDate nuevaFechaFin = currentFechaFin.plusDays(extension.getDiasExtension());

            // Re-verify overlap (race condition protection)
            if (rentaRepository.existsApprovedOverlap(vehicle.getId(), currentFechaFin.plusDays(1), nuevaFechaFin)) {
                throw new IllegalArgumentException("El vehículo ya tiene una renta aprobada durante el período de extensión");
            }

            // Update rental fin date
            renta.setFechaFin(nuevaFechaFin);
            rentaRepository.save(renta);
        }

        extension.setEstado(newStatus);
        return toResponse(rentaExtensionRepository.save(extension));
    }

    @Transactional(readOnly = true)
    public List<ExtensionResponse> getMyExtensions(String clientEmail) {
        Usuario cliente = usuarioRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + clientEmail));

        return rentaExtensionRepository.findByClienteId(cliente.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExtensionResponse> getReceivedExtensions(String ownerEmail) {
        Usuario owner = usuarioRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + ownerEmail));

        return rentaExtensionRepository.findByVehicleOwnerId(owner.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExtensionResponse> getExtensionsForRental(Long rentalId, String userEmail) {
        Usuario user = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + userEmail));

        Renta renta = rentaRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Renta no encontrada"));

        boolean isCliente = renta.getCliente().getId().equals(user.getId());
        boolean isOwner = renta.getVehicle().getOwner() != null && renta.getVehicle().getOwner().getId().equals(user.getId());

        if (!isCliente && !isOwner) {
            throw new IllegalArgumentException("No tienes permiso para ver las extensiones de esta renta");
        }

        return rentaExtensionRepository.findByRentaIdOrderByFechaSolicitudDesc(rentalId).stream()
                .map(this::toResponse)
                .toList();
    }

    private ExtensionResponse toResponse(RentaExtension extension) {
        Renta renta = extension.getRenta();
        Vehicle vehicle = renta.getVehicle();
        Usuario cliente = renta.getCliente();

        double pricePerDay = vehicle.getPricePerDay() != null ? vehicle.getPricePerDay() : 0.0;
        double costoAdicional = extension.getDiasExtension() * pricePerDay;

        return new ExtensionResponse(
                extension.getId(),
                renta.getId(),
                extension.getDiasExtension(),
                extension.getEstado(),
                extension.getFechaSolicitud(),
                extension.getFechaFinNueva(),
                costoAdicional,
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getImageUrl(),
                cliente.getNombre(),
                cliente.getEmail()
        );
    }
}
