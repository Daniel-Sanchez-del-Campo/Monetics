package com.monetics.moneticsback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de seguridad para el cifrado de contraseñas.
 *
 * Se separa de SecurityConfig para evitar dependencias circulares
 * con otros beans del sistema.
 */
@Configuration
public class PasswordConfig {

    /**
     * Bean encargado de cifrar contraseñas con BCrypt.
     *
     * Será inyectado en UsuarioService.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
