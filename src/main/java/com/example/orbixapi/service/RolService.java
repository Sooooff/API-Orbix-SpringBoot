package com.example.orbixapi.service;

import com.example.orbixapi.model.Rol;

import java.util.List;
import java.util.Optional;

public interface RolService {

    List<Rol> listar();

    Optional<Rol> buscarPorNombre(String nombre);

    Rol guardar(Rol rol);
}
