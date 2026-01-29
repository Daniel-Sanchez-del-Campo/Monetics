package com.monetics.moneticsback.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración principal de Spring Security.
 *
 * Este archivo define:
 * - Cómo se protege la API
 * - Qué endpoints son públicos y cuáles no
 * - Que la aplicación es STATELESS (sin sesiones)
 *
 * Es la base sobre la que luego se monta JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Define la cadena de filtros de seguridad.
     *
     * Aquí indicamos:
     * - Que no usamos sesiones
     * - Que desactivamos CSRF (API REST)
     * - Qué rutas son públicas
     * - Que el resto requiere autenticación
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Desactivamos CSRF porque usamos JWT y no sesiones
                .csrf(csrf -> csrf.disable())

                // Indicamos que la aplicación NO mantiene estado (stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configuración de permisos de acceso
                .authorizeHttpRequests(auth -> auth
                        // Endpoint público (login vendrá aquí)
                        .requestMatchers("/api/auth/**").permitAll()

                        // El resto de endpoints requieren autenticación
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     * AuthenticationManager se encarga de autenticar usuarios.
     *
     * Spring lo usa internamente cuando hagamos login
     * (email + password).
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * PasswordEncoder para cifrar contraseñas.
     *
     * Usamos BCrypt porque:
     * - es seguro
     * - es estándar
     * - es lo recomendado por Spring
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
