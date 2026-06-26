package com.example.orbixapi.repository;

import com.example.orbixapi.model.Rol;
import com.example.orbixapi.model.RolNombre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(RolNombre nombre);
}
