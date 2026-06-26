package com.example.orbixapi.controller;

import com.example.orbixapi.dto.AuthResponse;
import com.example.orbixapi.dto.LoginRequest;
import com.example.orbixapi.dto.RegisterRequest;
import com.example.orbixapi.model.Permiso;
import com.example.orbixapi.model.RolNombre;
import com.example.orbixapi.model.Usuario;
import com.example.orbixapi.repository.RolRepository;
import com.example.orbixapi.repository.UsuarioRepository;
import com.example.orbixapi.security.JwtAuthFilter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    public AuthController(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtAuthFilter jwtAuthFilter
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        var rol = rolRepository.findByNombre(RolNombre.ROLE_CLIENTE)
                .orElseThrow(() -> new IllegalStateException("Rol no encontrado: ROLE_CLIENTE"));

        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setNombre(request.nombre());
        usuario.setRoles(Set.of(rol));
        usuarioRepository.save(usuario);

        return toResponse(usuario);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.email()).orElseThrow();
        return toResponse(usuario);
    }

    @GetMapping("/me")
    public AuthResponse me(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return AuthResponse.of(null, usuario.getEmail(), roles(usuario), permissions(usuario));
    }

    private AuthResponse toResponse(Usuario usuario) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtAuthFilter.generateToken(userDetails);
        return AuthResponse.of(token, usuario.getEmail(), roles(usuario), permissions(usuario));
    }

    private List<String> roles(Usuario usuario) {
        return usuario.getRoles().stream()
                .map(rol -> rol.getNombre().name())
                .sorted()
                .collect(Collectors.toList());
    }

    private List<String> permissions(Usuario usuario) {
        return usuario.getRoles().stream()
                .flatMap(rol -> rol.getPermisos().stream())
                .map(Permiso::getCodigo)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
