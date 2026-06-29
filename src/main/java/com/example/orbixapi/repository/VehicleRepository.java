package com.example.orbixapi.repository;

import com.example.orbixapi.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v FROM Vehicle v JOIN FETCH v.owner")
    List<Vehicle> findAllWithOwner();

    @Query("SELECT v FROM Vehicle v JOIN FETCH v.owner WHERE v.owner.id = :ownerId ORDER BY v.id DESC")
    List<Vehicle> findByOwnerIdWithOwner(Long ownerId);

    @Query("SELECT v FROM Vehicle v JOIN FETCH v.owner WHERE v.id = :id")
    Optional<Vehicle> findByIdWithOwner(Long id);
}
