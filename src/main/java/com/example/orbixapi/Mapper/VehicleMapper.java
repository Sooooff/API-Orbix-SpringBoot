package com.example.orbixapi.dto;

import com.example.orbixapi.model.Vehicle;

public final class VehicleMapper {

    private VehicleMapper() {
    }

    public static Vehicle toEntity(VehicleDto dto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(dto.brand());
        vehicle.setModel(dto.model());
        vehicle.setYear(dto.year());
        vehicle.setTransmission(dto.transmission());
        vehicle.setPassengers(dto.passengers());
        vehicle.setPricePerDay(dto.pricePerDay());
        vehicle.setImageUrl(dto.imageUrl());
        vehicle.setAvailable(dto.available());
        return vehicle;
    }

    public static VehicleDto toDto(Vehicle vehicle) {
        return new VehicleDto(
                vehicle.getId(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getTransmission(),
                vehicle.getPassengers(),
                vehicle.getPricePerDay(),
                vehicle.getImageUrl(),
                vehicle.getAvailable()
        );
    }
}
