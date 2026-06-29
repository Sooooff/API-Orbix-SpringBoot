package com.example.orbixapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "renta_extensiones")
@Getter
@Setter
@NoArgsConstructor
public class RentaExtension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "renta_id", nullable = false)
    private Renta renta;

    @Column(nullable = false)
    private Integer diasExtension;

    @Column(nullable = false)
    private LocalDate fechaFinNueva;

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
