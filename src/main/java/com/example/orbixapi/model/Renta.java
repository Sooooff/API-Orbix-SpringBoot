package com.example.orbixapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentas")
@Getter
@Setter
@NoArgsConstructor
public class Renta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus estado = RentalStatus.PENDIENTE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    @PrePersist
    void onCreate() {
        if (fechaSolicitud == null) {
            fechaSolicitud = LocalDateTime.now();
        }
        if (estado == null) {
            estado = RentalStatus.PENDIENTE;
        }
    }
}
