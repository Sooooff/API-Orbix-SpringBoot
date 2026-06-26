package com.example.orbixapi.config;

import com.example.orbixapi.model.Permiso;
import com.example.orbixapi.model.Rol;
import com.example.orbixapi.model.RolNombre;
import com.example.orbixapi.repository.PermisoRepository;
import com.example.orbixapi.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PermisoRepository permisoRepository;
    private final RolRepository rolRepository;

    public DataSeeder(PermisoRepository permisoRepository, RolRepository rolRepository) {
        this.permisoRepository = permisoRepository;
        this.rolRepository = rolRepository;
    }

    @Override
    public void run(String... args) {
        seedPermisos();
        seedRoles();
    }

    private void seedPermisos() {
        Map<String, String> permisos = Map.ofEntries(
                Map.entry("vehicles:read", "Ver catálogo de vehículos"),
                Map.entry("vehicles:create", "Publicar vehículos"),
                Map.entry("vehicles:update:own", "Editar vehículos propios"),
                Map.entry("vehicles:update:any", "Editar cualquier vehículo"),
                Map.entry("vehicles:delete:own", "Eliminar vehículos propios"),
                Map.entry("rentals:create", "Crear reservas"),
                Map.entry("rentals:read:own", "Ver reservas propias"),
                Map.entry("users:manage", "Gestionar usuarios"),
                Map.entry("roles:manage", "Gestionar roles y permisos")
        );

        permisos.forEach((codigo, descripcion) -> {
            if (permisoRepository.findByCodigo(codigo).isEmpty()) {
                permisoRepository.save(new Permiso(null, codigo, descripcion));
            }
        });
    }

    private void seedRoles() {
        Map<RolNombre, List<String>> rolePermissions = Map.of(
                RolNombre.ROLE_CLIENTE, List.of(
                        "vehicles:read",
                        "rentals:create",
                        "rentals:read:own"
                ),
                RolNombre.ROLE_ARRENDADOR, List.of(
                        "vehicles:read",
                        "vehicles:create",
                        "vehicles:update:own",
                        "vehicles:delete:own",
                        "rentals:read:own"
                ),
                RolNombre.ROLE_ADMIN, List.of(
                        "vehicles:read",
                        "vehicles:create",
                        "vehicles:update:own",
                        "vehicles:update:any",
                        "vehicles:delete:own",
                        "rentals:create",
                        "rentals:read:own",
                        "users:manage",
                        "roles:manage"
                )
        );

        rolePermissions.forEach((rolNombre, permisoCodigos) -> {
            Rol rol = rolRepository.findByNombre(rolNombre).orElseGet(() -> {
                Rol nuevoRol = new Rol();
                nuevoRol.setNombre(rolNombre);
                return rolRepository.save(nuevoRol);
            });

            Set<Permiso> permisos = new HashSet<>();
            for (String codigo : permisoCodigos) {
                permisoRepository.findByCodigo(codigo).ifPresent(permisos::add);
            }
            rol.setPermisos(permisos);
            rolRepository.save(rol);
        });
    }
}
