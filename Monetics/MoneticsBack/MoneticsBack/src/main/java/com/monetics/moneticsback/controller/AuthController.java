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
 * Controller encargado de la autenticación de usuarios.
 *
 * Expone el endpoint de login y devuelve un JWT
 * si las credenciales son correctas.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
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
     * Endpoint de login.
     *
     * @param loginRequestDTO credenciales del usuario
     * @return token JWT + datos básicos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO loginRequestDTO
    ) {

        // 1️⃣ Autenticamos usando Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getEmail(),
                        loginRequestDTO.getPassword()
                )
        );

        // 2️⃣ Generamos el token JWT
        String token = jwtUtil.generarToken(
                (org.springframework.security.core.userdetails.User)
                        authentication.getPrincipal()
        );

        // 3️⃣ Obtenemos el usuario
        Usuario usuarioEntidad = usuarioService
                .obtenerUsuarioEntidadPorEmail(loginRequestDTO.getEmail());

        // 4️⃣ Devolvemos la respuesta con UsuarioDTO anidado
        return ResponseEntity.ok(
                new LoginResponseDTO(
                        token,
                        usuarioService.obtenerUsuarioDTO(usuarioEntidad.getIdUsuario())
                )
        );
    }

    /**
     * Endpoint de registro.
     *
     * @param crearUsuarioDTO datos del nuevo usuario
     * @return token JWT + datos básicos del usuario
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody CrearUsuarioDTO crearUsuarioDTO
    ) {

        // 1️⃣ Verificar que el email no exista
        if (usuarioService.existeEmail(crearUsuarioDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El email ya está registrado");
        }

        // 2️⃣ Crear el usuario (la contraseña se hashea automáticamente)
        Usuario nuevoUsuario = usuarioService.crearUsuario(crearUsuarioDTO);

        // 3️⃣ Autenticar automáticamente al usuario recién creado
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        crearUsuarioDTO.getEmail(),
                        crearUsuarioDTO.getPassword()
                )
        );

        // 4️⃣ Generar token JWT
        String token = jwtUtil.generarToken(
                (org.springframework.security.core.userdetails.User)
                        authentication.getPrincipal()
        );

        // 5️⃣ Devolver la respuesta con UsuarioDTO anidado
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new LoginResponseDTO(
                        token,
                        usuarioService.obtenerUsuarioDTO(nuevoUsuario.getIdUsuario())
                )
        );
    }
}
