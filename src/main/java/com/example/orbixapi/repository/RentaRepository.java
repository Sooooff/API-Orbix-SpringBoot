package com.example.orbixapi.repository;

import com.example.orbixapi.model.Renta;
import com.example.orbixapi.model.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RentaRepository extends JpaRepository<Renta, Long> {

    List<Renta> findByClienteIdOrderByFechaSolicitudDesc(Long clienteId);

    @Query("""
            SELECT r FROM Renta r
            WHERE r.vehicle.owner.id = :ownerId
            ORDER BY r.fechaSolicitud DESC
            """)
    List<Renta> findByVehicleOwnerIdOrderByFechaSolicitudDesc(Long ownerId);

    List<Renta> findByVehicleIdAndEstado(Long vehicleId, RentalStatus estado);

    @Query("""
            SELECT COUNT(r) > 0 FROM Renta r
            WHERE r.vehicle.id = :vehicleId
              AND r.estado = com.example.orbixapi.model.RentalStatus.APROBADA
              AND r.fechaInicio <= :fechaFin
              AND r.fechaFin >= :fechaInicio
            """)
    boolean existsApprovedOverlap(Long vehicleId, LocalDate fechaInicio, LocalDate fechaFin);

    @Query("""
            SELECT COUNT(r) > 0 FROM Renta r
            WHERE r.vehicle.owner.id = :ownerId
              AND r.cliente.id = :clienteId
              AND r.estado = com.example.orbixapi.model.RentalStatus.APROBADA
            """)
    boolean existsApprovedRentalBetween(Long ownerId, Long clienteId);
}
