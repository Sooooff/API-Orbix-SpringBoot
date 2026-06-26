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

    //Pasarlo a ENUM
    private String transmission;

    private String passengers;

    private Double pricePerDay;

    private String imageUrl;

    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Usuario owner;

    public Vehicle(){}
}
