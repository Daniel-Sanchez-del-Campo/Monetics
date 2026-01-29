package com.monetics.moneticsback.security;

import com.monetics.moneticsback.model.Usuario;
import com.monetics.moneticsback.service.UsuarioService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Implementación personalizada de UserDetailsService.
 *
 * Este servicio le dice a Spring Security:
 * - cómo cargar un usuario desde la base de datos
 * - qué campo usamos como username (email)
 * - qué roles tiene el usuario
 *
 * Es el puente entre Spring Security y nuestra entidad Usuario.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioService usuarioService;

    public CustomUserDetailsService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Método que Spring Security llama automáticamente
     * cuando necesita autenticar un usuario.
     *
     * @param username en nuestro caso es el EMAIL
     * @return UserDetails que Spring Security entiende
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // Obtenemos el usuario como entidad desde nuestro dominio
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(username);

        // Convertimos el rol del usuario a un formato que Spring entiende
        GrantedAuthority authority =
                new SimpleGrantedAuthority(usuario.getRol().name());

        // Devolvemos un UserDetails estándar de Spring
        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),          // username
                usuario.getPassword(),       // password cifrado
                usuario.getActivo(),          // enabled
                true,                         // accountNonExpired
                true,                         // credentialsNonExpired
                true,                         // accountNonLocked
                Collections.singleton(authority) // roles
        );
    }
}
