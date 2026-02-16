package com.monetics.moneticsback.controller;

import com.monetics.moneticsback.dto.CrearUsuarioDTO;
import com.monetics.moneticsback.dto.LoginRequestDTO;
import com.monetics.moneticsback.dto.LoginResponseDTO;
import com.monetics.moneticsback.model.Usuario;
import com.monetics.moneticsback.security.JwtUtil;
import com.monetics.moneticsback.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * ========================== AUTH CONTROLLER ==========================
 * Controller REST encargado de la autenticación (login y registro).
 *
 * CONCEPTOS CLAVE PARA LA PRESENTACIÓN:
 *
 * ¿POR QUÉ ESTE CONTROLLER ES PÚBLICO?
 * - En SecurityConfig.java configuramos:
 *   .requestMatchers("/api/auth/**").permitAll()
 *   Esto permite que CUALQUIER persona acceda a /api/auth/login y /api/auth/register
 *   SIN necesidad de un token JWT (porque aún no lo tienen).
 *
 * ¿QUÉ DEVUELVE?
 * - Ambos endpoints devuelven un LoginResponseDTO que contiene:
 *   · token:   el JWT generado (el frontend lo guarda para futuras peticiones)
 *   · usuario: datos básicos del usuario (id, nombre, email, rol)
 *
 * FLUJO DE LOGIN:
 *   1) Frontend envía POST /api/auth/login con { email, password }
 *   2) AuthenticationManager autentica (compara con BD usando BCrypt)
 *   3) Si es correcto → se genera un JWT con JwtUtil
 *   4) Se devuelve el JWT + datos del usuario
 *   5) Frontend almacena el JWT (típicamente en localStorage)
 *   6) En cada petición futura, el frontend envía el JWT en el header Authorization
 *
 * FLUJO DE REGISTRO:
 *   1) Frontend envía POST /api/auth/register con { nombre, email, password, rol }
 *   2) Se verifica que el email no exista ya
 *   3) Se crea el usuario (contraseña se hashea automáticamente con BCrypt)
 *   4) Se autentica automáticamente al nuevo usuario
 *   5) Se genera un JWT y se devuelve → el usuario queda logueado directamente
 * ======================================================================
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * AuthenticationManager → orquestador de la autenticación de Spring Security.
     * Lo usamos para autenticar credenciales (email + password) contra la BD.
     * Internamente delega en DaoAuthenticationProvider → CustomUserDetailsService → BD.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * JwtUtil → utilidad para generar y validar tokens JWT.
     * Aquí solo la usamos para GENERAR tokens después de un login exitoso.
     */
    private final JwtUtil jwtUtil;

    /**
     * UsuarioService → servicio de negocio para operaciones con usuarios.
     * Lo usamos para crear usuarios y obtener DTOs.
     */
    private final UsuarioService usuarioService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UsuarioService usuarioService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    /**
     * ==================== ENDPOINT DE LOGIN ====================
     * POST /api/auth/login
     *
     * PROCESO PASO A PASO:
     *
     * 1) Recibe las credenciales del frontend (email + password) en el body como JSON.
     *
     * 2) Crea un UsernamePasswordAuthenticationToken:
     *    - Es un objeto de Spring Security que encapsula las credenciales.
     *    - NO es un JWT, es solo un "sobre" interno de Spring para pasar email+password.
     *
     * 3) Llama a authenticationManager.authenticate():
     *    - El AuthenticationManager delega en DaoAuthenticationProvider.
     *    - El provider llama a CustomUserDetailsService.loadUserByUsername(email).
     *    - El servicio busca al usuario en la BD.
     *    - El provider compara la contraseña del formulario con la hasheada (BCrypt).
     *    - Si coinciden → devuelve un Authentication con los datos del usuario.
     *    - Si NO coinciden → lanza BadCredentialsException → Spring devuelve 401.
     *
     * 4) authentication.getPrincipal() → devuelve el UserDetails del usuario autenticado.
     *    Lo usamos para generar el JWT con sus roles.
     *
     * 5) Devolvemos el JWT + datos del usuario al frontend.
     *
     * @param loginRequestDTO credenciales del usuario (email + password)
     * @return 200 OK con LoginResponseDTO (token + usuario)
     *         401 UNAUTHORIZED si las credenciales son incorrectas
     * ==================================================================
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO loginRequestDTO
    ) {

        // PASO 1: Autenticamos usando Spring Security
        // Internamente: email → BD → compara contraseña con BCrypt
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getEmail(),
                        loginRequestDTO.getPassword()
                )
        );

        // PASO 2: Generamos el token JWT con los datos del usuario autenticado
        // El token contendrá: email (subject), rol (claim), fecha expiración
        String token = jwtUtil.generarToken(
                (org.springframework.security.core.userdetails.User)
                        authentication.getPrincipal()
        );

        // PASO 3: Obtenemos la entidad del usuario para construir el DTO de respuesta
        Usuario usuarioEntidad = usuarioService
                .obtenerUsuarioEntidadPorEmail(loginRequestDTO.getEmail());

        // PASO 4: Devolvemos la respuesta con el JWT y los datos del usuario
        // El frontend almacenará el token y lo enviará en cada petición futura
        return ResponseEntity.ok(
                new LoginResponseDTO(
                        token,
                        usuarioService.obtenerUsuarioDTO(usuarioEntidad.getIdUsuario())
                )
        );
    }

    /**
     * ==================== ENDPOINT DE REGISTRO ====================
     * POST /api/auth/register
     *
     * PROCESO PASO A PASO:
     *
     * 1) Verifica que el email no exista ya en la BD (evita duplicados).
     *    Si ya existe → devuelve 409 CONFLICT.
     *
     * 2) Crea el usuario en la BD a través de UsuarioService.
     *    La contraseña se hashea AUTOMÁTICAMENTE con BCrypt en el servicio
     *    antes de guardarla. NUNCA se almacena la contraseña en texto plano.
     *
     * 3) Autentica automáticamente al usuario recién creado.
     *    Usamos la contraseña en texto plano (crearUsuarioDTO.getPassword())
     *    porque aún la tenemos disponible en el DTO de entrada.
     *    Spring la comparará con la versión hasheada que acabamos de guardar.
     *
     * 4) Genera un JWT para el nuevo usuario → queda logueado directamente.
     *    El usuario NO necesita ir a la pantalla de login después de registrarse.
     *
     * @param crearUsuarioDTO datos del nuevo usuario (nombre, email, password, rol)
     * @return 201 CREATED con LoginResponseDTO (token + usuario)
     *         409 CONFLICT si el email ya está registrado
     * ==================================================================
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody CrearUsuarioDTO crearUsuarioDTO
    ) {

        // PASO 1: Verificar que el email no exista ya en la BD
        if (usuarioService.existeEmail(crearUsuarioDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El email ya está registrado");
        }

        // PASO 2: Crear el usuario (la contraseña se hashea con BCrypt en UsuarioService)
        Usuario nuevoUsuario = usuarioService.crearUsuario(crearUsuarioDTO);

        // PASO 3: Autenticar automáticamente al usuario recién creado
        // Usamos la contraseña original (texto plano) del DTO
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        crearUsuarioDTO.getEmail(),
                        crearUsuarioDTO.getPassword()
                )
        );

        // PASO 4: Generar token JWT para el nuevo usuario
        String token = jwtUtil.generarToken(
                (org.springframework.security.core.userdetails.User)
                        authentication.getPrincipal()
        );

        // PASO 5: Devolver 201 CREATED con el JWT y los datos del usuario
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new LoginResponseDTO(
                        token,
                        usuarioService.obtenerUsuarioDTO(nuevoUsuario.getIdUsuario())
                )
        );
    }
}
