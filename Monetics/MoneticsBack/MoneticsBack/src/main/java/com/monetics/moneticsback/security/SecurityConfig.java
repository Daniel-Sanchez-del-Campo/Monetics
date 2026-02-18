package com.monetics.moneticsback.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

/**
 * ========================== SECURITY CONFIG ==========================
 * Clase central de Spring Security. Aquí se define TODA la política de
 * seguridad de la aplicación.
 *
 * CONCEPTOS CLAVE PARA LA PRESENTACIÓN:
 *
 * 1) @Configuration  → Indica a Spring que esta clase contiene beans de configuración.
 * 2) @EnableWebSecurity → Activa Spring Security y permite personalizar la seguridad HTTP.
 *    Sin esta anotación, Spring usaría una configuración por defecto (formulario de login,
 *    sesiones con cookies, etc.) que NO nos sirve para una API REST.
 *
 * 3) Esta clase configura 3 componentes fundamentales:
 *    - DaoAuthenticationProvider → el "verificador" de credenciales contra la base de datos.
 *    - AuthenticationManager     → el orquestador que gestiona la autenticación.
 *    - SecurityFilterChain       → la cadena de filtros HTTP que protege los endpoints.
 *
 * ARQUITECTURA:
 *    Petición HTTP → CORS → SecurityFilterChain → JwtFilter → Controller
 * =====================================================================
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Filtro JWT personalizado que valida el token en cada petición (ver JwtAuthenticationFilter.java)
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Servicio que carga usuarios desde la BD (ver CustomUserDetailsService.java)
    private final CustomUserDetailsService userDetailsService;

    // Codificador BCrypt para comparar contraseñas (ver PasswordConfig.java)
    private final PasswordEncoder passwordEncoder;

    // Spring inyecta automáticamente las dependencias por constructor (Inyección de Dependencias)
    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * ==================== DaoAuthenticationProvider ====================
     * Bean que conecta Spring Security con nuestra base de datos.
     *
     * ¿QUÉ HACE?
     * - Cuando el usuario envía email + contraseña en el login,
     *   este provider se encarga de:
     *   1) Llamar a CustomUserDetailsService.loadUserByUsername(email)
     *      para buscar al usuario en la BD.
     *   2) Usar el PasswordEncoder (BCrypt) para comparar la contraseña
     *      enviada con la contraseña hasheada almacenada en la BD.
     *   3) Si coinciden → autenticación exitosa.
     *      Si no coinciden → lanza BadCredentialsException (401).
     *
     * ¿POR QUÉ "Dao"?
     * - "DAO" = Data Access Object. Indica que la autenticación se resuelve
     *   consultando una fuente de datos (en nuestro caso, MySQL a través de JPA).
     * ==================================================================
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        // Le decimos AL provider DÓNDE buscar usuarios → nuestro CustomUserDetailsService
        provider.setUserDetailsService(userDetailsService);

        // Le decimos CÓMO comparar contraseñas → BCrypt (nunca texto plano)
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    /**
     * ==================== AuthenticationManager ====================
     * Bean que actúa como "director de orquesta" de la autenticación.
     *
     * ¿QUÉ HACE?
     * - Es el punto de entrada para autenticar usuarios desde el código.
     *   En AuthController.java lo usamos así:
     *     authenticationManager.authenticate(
     *         new UsernamePasswordAuthenticationToken(email, password)
     *     );
     *
     * - Él delega en el DaoAuthenticationProvider (definido arriba)
     *   para hacer la verificación real.
     *
     * ¿POR QUÉ SE CONSTRUYE EXPLÍCITAMENTE?
     * - En Spring Security 6+ ya no se configura automáticamente.
     *   Debemos construirlo manualmente usando AuthenticationManagerBuilder
     *   y registrar nuestro provider.
     * ================================================================
     */
    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http
    ) throws Exception {

        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        // Registramos nuestro provider (BD + BCrypt) en el manager
        authBuilder.authenticationProvider(authenticationProvider());

        return authBuilder.build();
    }

    /**
     * ==================== SecurityFilterChain ====================
     * Bean que define la CADENA DE FILTROS de seguridad HTTP.
     * Es el corazón de la configuración: decide qué peticiones se permiten,
     * cuáles requieren autenticación y cómo se gestionan las sesiones.
     *
     * FLUJO DE UNA PETICIÓN:
     *   1) Llega la petición HTTP
     *   2) Se aplican los filtros de la cadena en orden
     *   3) Nuestro JwtAuthenticationFilter se ejecuta ANTES del
     *      filtro estándar de Spring (UsernamePasswordAuthenticationFilter)
     *   4) Si el JWT es válido → se establece la autenticación en el SecurityContext
     *   5) La petición llega al Controller
     * ================================================================
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                /**
                 * CSRF (Cross-Site Request Forgery) → DESACTIVADO
                 *
                 * ¿QUÉ ES CSRF?
                 * - Es un ataque donde una web maliciosa envía peticiones al backend
                 *   aprovechando la cookie de sesión del usuario.
                 *
                 * ¿POR QUÉ LO DESACTIVAMOS?
                 * - Porque NO usamos cookies ni sesiones. Usamos JWT en el header
                 *   "Authorization", que NO se envía automáticamente como las cookies.
                 *   Por lo tanto, el ataque CSRF no es posible en nuestra arquitectura.
                 * - En APIs REST stateless, desactivar CSRF es la práctica estándar.
                 */
                .cors(Customizer.withDefaults())

                .csrf(csrf -> csrf.disable())

                /**
                 * POLÍTICA DE SESIONES → STATELESS (sin estado)
                 *
                 * ¿QUÉ SIGNIFICA STATELESS?
                 * - Spring Security NO creará HttpSession en el servidor.
                 * - NO se almacena información del usuario entre peticiones.
                 * - Cada petición debe incluir su propio token JWT para autenticarse.
                 *
                 * ¿POR QUÉ STATELESS?
                 * - Escalabilidad: como el servidor no guarda sesiones, podemos tener
                 *   múltiples instancias del backend sin compartir estado.
                 * - Es el estándar para APIs REST consumidas por frontends SPA (Angular).
                 *
                 * ALTERNATIVA (que NO usamos):
                 * - SessionCreationPolicy.IF_REQUIRED → crea sesiones (apps web tradicionales con JSP).
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                /**
                 * REGLAS DE AUTORIZACIÓN → qué endpoints son públicos y cuáles protegidos
                 *
                 * permitAll()      → cualquier persona puede acceder, sin token.
                 * authenticated()  → requiere un JWT válido en el header Authorization.
                 */
                .authorizeHttpRequests(auth -> auth
                        // ENDPOINTS PÚBLICOS (no requieren autenticación):
                        // - /api/auth/** → login y registro (el usuario aún no tiene token)
                        // - /api/test/** → endpoints de prueba
                        .requestMatchers("/api/auth/**", "/api/test/**").permitAll()

                        // SWAGGER/OPENAPI → documentación de la API (público para desarrollo)
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // TODOS LOS DEMÁS ENDPOINTS → requieren JWT válido
                        .anyRequest().authenticated()
                )

                /**
                 * REGISTRO DEL FILTRO JWT
                 *
                 * addFilterBefore() → inserta nuestro JwtAuthenticationFilter ANTES del
                 * filtro estándar UsernamePasswordAuthenticationFilter de Spring.
                 *
                 * ¿POR QUÉ ANTES?
                 * - Porque queremos que nuestro filtro intercepte la petición primero,
                 *   extraiga el JWT del header, lo valide y establezca la autenticación
                 *   en el SecurityContext ANTES de que Spring intente autenticar por
                 *   su cuenta con usuario/contraseña (que es el comportamiento por defecto).
                 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
