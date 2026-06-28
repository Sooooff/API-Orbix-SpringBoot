package com.example.orbixapi.repository;

import com.example.orbixapi.model.ResenaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResenaUsuarioRepository extends JpaRepository<ResenaUsuario, Long> {

    List<ResenaUsuario> findByReviewedIdOrderByFechaDesc(Long reviewedId);

    Optional<ResenaUsuario> findByReviewerIdAndReviewedId(Long reviewerId, Long reviewedId);

    long countByReviewedId(Long reviewedId);

    @Query("SELECT AVG(r.rating) FROM ResenaUsuario r WHERE r.reviewed.id = :reviewedId")
    Double averageRatingByReviewedId(Long reviewedId);
}
