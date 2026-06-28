package com.example.orbixapi.repository;

import com.example.orbixapi.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByVehicleIdOrderByFechaDesc(Long vehicleId);

    Optional<Resena> findByReviewerIdAndVehicleId(Long reviewerId, Long vehicleId);

    long countByVehicleId(Long vehicleId);

    @Query("SELECT AVG(r.rating) FROM Resena r WHERE r.vehicle.id = :vehicleId")
    Double averageRatingByVehicleId(Long vehicleId);
}
