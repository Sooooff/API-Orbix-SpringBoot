package com.example.orbixapi.repository;

import com.example.orbixapi.model.ResenaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResenaUsuarioRepository extends JpaRepository<ResenaUsuario, Long> {

    @Query("""
            SELECT r FROM ResenaUsuario r
            JOIN FETCH r.reviewer
            JOIN FETCH r.reviewed
            WHERE r.reviewed.id = :reviewedId
            ORDER BY r.fecha DESC
            """)
    List<ResenaUsuario> findByReviewedIdWithUsersOrderByFechaDesc(Long reviewedId);

    List<ResenaUsuario> findByReviewedIdOrderByFechaDesc(Long reviewedId);

    Optional<ResenaUsuario> findByReviewerIdAndReviewedId(Long reviewerId, Long reviewedId);

    long countByReviewedId(Long reviewedId);

    @Query("SELECT AVG(r.rating) FROM ResenaUsuario r WHERE r.reviewed.id = :reviewedId")
    Double averageRatingByReviewedId(Long reviewedId);
}
