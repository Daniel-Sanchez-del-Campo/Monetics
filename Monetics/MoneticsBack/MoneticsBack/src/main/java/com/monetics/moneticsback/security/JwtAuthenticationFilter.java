package com.monetics.moneticsback.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ========================== JWT AUTHENTICATION FILTER ==========================
 * Filtro de seguridad que se ejecuta en CADA petición HTTP (excepto las excluidas).
 *
 * CONCEPTOS CLAVE PARA LA PRESENTACIÓN:
 *
 * ¿QUÉ ES UN FILTRO EN SPRING SECURITY?
 * - Spring Security funciona con una "cadena de filtros" (Filter Chain).
 * - Cada petición HTTP pasa por múltiples filtros antes de llegar al Controller.
 * - Nosotros creamos este filtro PERSONALIZADO para interceptar las peticiones,
 *   extraer el JWT del header y validarlo.
 *
 * ¿POR QUÉ EXTENDEMOS OncePerRequestFilter?
 * - Garantiza que el filtro se ejecute UNA SOLA VEZ por petición.
 * - En algunas configuraciones, un filtro podría ejecutarse múltiples veces
 *   (por ejemplo, si hay redirecciones internas). OncePerRequestFilter lo evita.
 *
 * ¿DÓNDE SE REGISTRA ESTE FILTRO?
 * - En SecurityConfig.java, con addFilterBefore():
 *   .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
 *   Esto lo coloca ANTES del filtro estándar de usuario/contraseña de Spring.
 *
 * FLUJO COMPLETO DE UNA PETICIÓN AUTENTICADA:
 *   1) Cliente envía: GET /api/gastos + Header "Authorization: Bearer eyJhbG..."
 *   2) Este filtro intercepta la petición
 *   3) Extrae el token del header (quita el prefijo "Bearer ")
 *   4) Extrae el email del token usando JwtUtil
 *   5) Busca al usuario en la BD usando CustomUserDetailsService
 *   6) Valida que la firma del token sea correcta y no haya expirado
 *   7) Si es válido → crea un objeto de autenticación y lo pone en el SecurityContext
 *   8) La petición continúa hasta el Controller, que ya sabe QUIÉN es el usuario
 * ================================================================================
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Utilidad para operaciones con JWT (generar, validar, extraer claims)
    private final JwtUtil jwtUtil;

    // Servicio para cargar usuarios desde la BD
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * ==================== RUTAS EXCLUIDAS DEL FILTRO ====================
     * Define qué rutas NO deben pasar por la validación JWT.
     *
     * ¿POR QUÉ SE EXCLUYEN?
     * - /api/auth/  → Son los endpoints de login y registro. El usuario todavía
     *                  NO tiene token cuando intenta hacer login, así que no podemos
     *                  pedirle uno.
     * - /swagger-ui → Documentación de la API (solo para desarrollo).
     * - /v3/api-docs → Esquema OpenAPI (usado por Swagger).
     * - /error       → Página de error de Spring Boot.
     *
     * Si retorna TRUE → el filtro NO se ejecuta para esa ruta.
     * ==================================================================
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.startsWith("/api/auth/")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.equals("/error");
    }

    /**
     * ==================== LÓGICA PRINCIPAL DEL FILTRO ====================
     * Método que se ejecuta para cada petición que NO esté excluida.
     *
     * PARÁMETROS:
     * - request:     la petición HTTP entrante (contiene headers, URL, etc.)
     * - response:    la respuesta HTTP (podríamos modificarla, pero no lo hacemos)
     * - filterChain: la cadena de filtros. Llamar a filterChain.doFilter() permite
     *                que la petición continúe al siguiente filtro o al Controller.
     *
     * ¿QUÉ ES EL SecurityContext?
     * - Es un "contenedor" donde Spring Security guarda la info del usuario autenticado.
     * - Es un objeto ThreadLocal: cada hilo (petición) tiene su propio contexto.
     * - Los Controllers pueden acceder a él para saber quién está haciendo la petición.
     * - Al ser STATELESS, el SecurityContext se crea y destruye en cada petición.
     * ==================================================================
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // PASO 1: Extraer el header "Authorization" de la petición
        // El frontend lo envía así: "Authorization: Bearer eyJhbGciOiJIUzI1..."
        final String authHeader = request.getHeader("Authorization");

        String jwt = null;
        String username = null;

        // PASO 2: Verificar que el header existe y tiene el formato correcto
        // "Bearer " tiene 7 caracteres → substring(7) extrae solo el token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            // PASO 3: Extraer el email (username) del payload del token
            username = jwtUtil.obtenerUsernameDelToken(jwt);
        }

        // PASO 4: Si tenemos un username Y no hay autenticación previa en el contexto
        // (evitamos re-autenticar si ya se procesó en otro filtro)
        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            // PASO 5: Cargar los datos completos del usuario desde la BD
            // Esto incluye: email, contraseña hasheada, rol, estado activo
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            // PASO 6: Validar el token (firma correcta + no expirado + username coincide)
            if (jwtUtil.validarToken(jwt, userDetails)) {

                // PASO 7: Crear el objeto de autenticación de Spring Security
                // UsernamePasswordAuthenticationToken contiene:
                //   - principal:   el UserDetails del usuario (quién es)
                //   - credentials: null (no necesitamos la contraseña, ya validamos con JWT)
                //   - authorities: los roles del usuario (ROLE_USER, ROLE_MANAGER, etc.)
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Añadimos detalles de la petición (IP, session ID, etc.)
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // PASO 8: Establecer la autenticación en el SecurityContext
                // A partir de aquí, Spring Security "sabe" que este usuario está autenticado
                // y los Controllers pueden acceder a sus datos
                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
            }
        }

        // PASO 9: Continuar con la cadena de filtros → la petición llega al Controller
        // Si el token no era válido, el SecurityContext queda vacío y Spring Security
        // devolverá automáticamente un 401 Unauthorized
        filterChain.doFilter(request, response);
    }
}
