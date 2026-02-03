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
 * Este servicio le indica a Spring Security:
 * - cómo cargar un usuario desde la base de datos
 * - qué campo usamos como username (email)
 * - qué roles tiene el usuario
 *
 * Usa métodos INTERNOS del UsuarioService que devuelven ENTIDADES.
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
     * @param username email del usuario
     * @return UserDetails que Spring Security entiende
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // 🔧 AQUÍ ESTABA EL ERROR:
        // Antes: obtenerUsuarioPorEmail(username)
        // Ahora: obtenerUsuarioEntidadPorEmail(username)

        Usuario usuario =
                usuarioService.obtenerUsuarioEntidadPorEmail(username);

        // Convertimos el rol a un formato que Spring Security entiende
        GrantedAuthority authority =
                new SimpleGrantedAuthority(usuario.getRol().name());

        // Devolvemos un UserDetails estándar
        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),      // username
                usuario.getPassword(),   // password cifrada
                usuario.getActivo(),     // enabled
                true,                    // accountNonExpired
                true,                    // credentialsNonExpired
                true,                    // accountNonLocked
                Collections.singleton(authority)
        );
    }
}
