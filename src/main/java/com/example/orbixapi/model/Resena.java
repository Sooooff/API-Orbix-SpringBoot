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
        name = "resenas",
        uniqueConstraints = @UniqueConstraint(columnNames = {"reviewer_id", "vehicle_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Usuario reviewer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private int rating;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "resena_tags", joinColumns = @JoinColumn(name = "resena_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private List<ReviewTag> tags = new ArrayList<>();

    @Column(length = 1000)
    private String comment;

    @Column(name = "fecha", updatable = false)
    private LocalDateTime fecha;

    @PrePersist
    void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
