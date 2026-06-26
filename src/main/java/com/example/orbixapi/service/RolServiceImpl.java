package com.example.orbixapi.service;

import com.example.orbixapi.model.Rol;
import com.example.orbixapi.model.RolNombre;
import com.example.orbixapi.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImpl implements RolService {
    private final RolRepository rolRepository;

    public RolServiceImpl(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    @Override
    public Optional<Rol> buscarPorNombre(String nombre) {
        return rolRepository.findByNombre(RolNombre.valueOf(nombre));
    }

    @Override
    public Rol guardar(Rol rol) {
        return rolRepository.save(rol);
    }
}
