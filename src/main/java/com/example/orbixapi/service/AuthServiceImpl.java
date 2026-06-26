package com.example.orbixapi.service;

import com.example.orbixapi.dto.AuthResponse;
import com.example.orbixapi.dto.LoginRequest;
import com.example.orbixapi.dto.RegisterRequest;
import com.example.orbixapi.model.Rol;
import com.example.orbixapi.model.RolNombre;
import com.example.orbixapi.model.Usuario;
import com.example.orbixapi.repository.RolRepository;
import com.example.orbixapi.repository.UsuarioRepository;
import com.example.orbixapi.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        RolNombre rolNombre = RolNombre.ROLE_CLIENTE;
        Rol rol = rolRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new IllegalStateException("Rol no encontrado: " + rolNombre));

        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setNombre(request.nombre());
        usuario.setRoles(Set.of(rol));

        usuarioRepository.save(usuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.of(
                token,
                usuario.getEmail(),
                usuario.getRoles().stream().map(r -> r.getNombre().name()).collect(Collectors.toList())
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(userDetails);

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow();

        return AuthResponse.of(
                token,
                usuario.getEmail(),
                usuario.getRoles().stream().map(r -> r.getNombre().name()).collect(Collectors.toList())
        );
    }
}
