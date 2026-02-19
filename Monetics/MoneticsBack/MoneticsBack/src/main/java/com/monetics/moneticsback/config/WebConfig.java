package com.monetics.moneticsback.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ========================== WEB CONFIG (CORS) ==========================
 * Configuración de CORS (Cross-Origin Resource Sharing).
 *
 * CONCEPTOS CLAVE PARA LA PRESENTACIÓN:
 *
 * ¿QUÉ ES CORS?
 * - Es una política de seguridad de los navegadores que BLOQUEA las peticiones
 *   HTTP entre diferentes orígenes (dominios, puertos o protocolos).
 *
 * ¿POR QUÉ EXISTE?
 * - Sin CORS, una web maliciosa podría hacer peticiones a nuestra API
 *   desde el navegador del usuario sin su consentimiento.
 * - El navegador bloquea estas peticiones por defecto para proteger al usuario.
 *
 * ¿POR QUÉ LO NECESITAMOS?
 * - Nuestro frontend Angular corre en http://localhost:4200
 * - Nuestro backend Spring corre en http://localhost:8080
 * - Como son PUERTOS DIFERENTES, el navegador los considera "orígenes distintos"
 *   y bloquea las peticiones del frontend al backend.
 * - Con esta configuración le decimos al navegador: "las peticiones desde
 *   localhost:4200 son de confianza, déjalas pasar".
 *
 * ¿CÓMO FUNCIONA TÉCNICAMENTE?
 * 1) El navegador envía primero una petición OPTIONS (preflight) preguntando:
 *    "¿puedo hacer un POST desde localhost:4200 con estos headers?"
 * 2) El servidor responde con las cabeceras CORS configuradas aquí.
 * 3) Si el origen está permitido → el navegador permite la petición real.
 *    Si no está permitido → el navegador la BLOQUEA (ni siquiera llega al servidor).
 *
 * RELACIÓN CON SPRING SECURITY:
 * - Esta configuración es COMPLEMENTARIA a SecurityConfig.
 * - SecurityConfig protege QUIÉN puede acceder (autenticación/autorización).
 * - WebConfig protege DESDE DÓNDE pueden llegar las peticiones (orígenes).
 * ======================================================================
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configura las reglas CORS para todas las rutas de la API.
     *
     * @param registry registro de configuraciones CORS de Spring
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")                // Aplica a todos los endpoints bajo /api/
                .allowedOrigins("http://localhost:4200", "http://localhost:4201") // Solo permite peticiones desde Angular (desarrollo)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*")                   // Permite cualquier header (incluido Authorization con el JWT)
                .allowCredentials(true)                // Permite enviar cookies/credenciales (necesario para Authorization header)
                .maxAge(3600);                         // Cache de la respuesta preflight: 1 hora (evita repetir OPTIONS)
    }
}
