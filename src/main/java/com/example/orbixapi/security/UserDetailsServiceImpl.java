package com.example.orbixapi.security;

import com.example.orbixapi.model.Permiso;
import com.example.orbixapi.model.Rol;
import com.example.orbixapi.model.Usuario;
import com.example.orbixapi.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (Rol rol : usuario.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(rol.getNombre().name()));
            for (Permiso permiso : rol.getPermisos()) {
                authorities.add(new SimpleGrantedAuthority(permiso.getCodigo()));
            }
        }

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.isEnabled(),
                true,
                true,
                true,
                authorities
        );
    }
}
