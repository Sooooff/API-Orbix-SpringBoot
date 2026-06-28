package com.example.orbixapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "resenas_usuario",
        uniqueConstraints = @UniqueConstraint(columnNames = {"reviewer_id", "reviewed_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class ResenaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Usuario reviewer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewed_id", nullable = false)
    private Usuario reviewed;

    @Column(nullable = false)
    private int rating;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "resena_usuario_tags", joinColumns = @JoinColumn(name = "resena_usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private List<ReviewTag> tags = new ArrayList<>();

    @Column(length = 1000)
    private String comment;

    @Column(name = "fecha", nullable = false, updatable = false)
    private LocalDateTime fecha;

    @PrePersist
    void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
