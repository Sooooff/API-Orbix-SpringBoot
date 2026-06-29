package com.example.orbixapi.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="vehicles")
@Getter
@Setter
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;

    private String model;

    private String year;

    @Enumerated(EnumType.STRING)
    @Column(name = "transmision")
    private Transmission transmission;

    private String passengers;

    private Double pricePerDay;

    private String imageUrl;

    private Boolean available;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria")
    private VehicleCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Usuario owner;

    public Vehicle(){}
}
