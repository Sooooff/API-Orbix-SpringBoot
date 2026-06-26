package com.example.orbixapi.config;

import com.example.orbixapi.model.Permiso;
import com.example.orbixapi.model.Rol;
import com.example.orbixapi.model.RolNombre;
import com.example.orbixapi.model.Usuario;
import com.example.orbixapi.repository.PermisoRepository;
import com.example.orbixapi.repository.RolRepository;
import com.example.orbixapi.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final String SEED_PASSWORD = "password123";

    private final PermisoRepository permisoRepository;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            PermisoRepository permisoRepository,
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.permisoRepository = permisoRepository;
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedPermisos();
        seedRoles();
        seedUsuarios();
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

    private void seedUsuarios() {
        Map<String, RolNombre> usuarios = Map.of(
                "cliente@orbix.com", RolNombre.ROLE_CLIENTE,
                "arrendador@orbix.com", RolNombre.ROLE_ARRENDADOR,
                "admin@orbix.com", RolNombre.ROLE_ADMIN
        );

        usuarios.forEach((email, rolNombre) -> {
            if (usuarioRepository.existsByEmail(email)) {
                return;
            }

            Rol rol = rolRepository.findByNombre(rolNombre)
                    .orElseThrow(() -> new IllegalStateException("Rol no encontrado: " + rolNombre));

            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setPassword(passwordEncoder.encode(SEED_PASSWORD));
            usuario.setNombre(email.split("@")[0]);
            usuario.setRoles(Set.of(rol));
            usuarioRepository.save(usuario);
        });
    }
}
