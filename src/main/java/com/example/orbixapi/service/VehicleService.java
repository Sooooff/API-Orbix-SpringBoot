package com.example.orbixapi.service;

import com.example.orbixapi.dto.VehicleDto;
import com.example.orbixapi.dto.VehicleMapper;
import com.example.orbixapi.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    public List<VehicleDto> getAll() {
        return repository.findAll().stream()
                .map(VehicleMapper::toDto)
                .toList();
    }

    public VehicleDto save(VehicleDto dto) {
        return VehicleMapper.toDto(repository.save(VehicleMapper.toEntity(dto)));
    }
}
