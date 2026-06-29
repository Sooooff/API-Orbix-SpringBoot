package com.example.orbixapi.repository;

import com.example.orbixapi.model.RentaExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentaExtensionRepository extends JpaRepository<RentaExtension, Long> {

    List<RentaExtension> findByRentaIdOrderByFechaSolicitudDesc(Long rentaId);

    @Query("""
            SELECT re FROM RentaExtension re
            WHERE re.renta.vehicle.owner.id = :ownerId
            ORDER BY re.fechaSolicitud DESC
            """)
    List<RentaExtension> findByVehicleOwnerId(Long ownerId);

    @Query("""
            SELECT re FROM RentaExtension re
            WHERE re.renta.cliente.id = :clienteId
            ORDER BY re.fechaSolicitud DESC
            """)
    List<RentaExtension> findByClienteId(Long clienteId);
}
