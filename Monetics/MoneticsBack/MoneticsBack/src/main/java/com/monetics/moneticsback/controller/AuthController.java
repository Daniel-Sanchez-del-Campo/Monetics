package com.monetics.moneticsback.controller;

import com.monetics.moneticsback.dto.LoginRequestDTO;
import com.monetics.moneticsback.dto.LoginResponseDTO;
import com.monetics.moneticsback.model.Usuario;
import com.monetics.moneticsback.security.JwtUtil;
import com.monetics.moneticsback.service.UsuarioService;
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

        // 3️⃣ Obtenemos el usuario como ENTIDAD (uso interno)
        Usuario usuario = usuarioService
                .obtenerUsuarioEntidadPorEmail(loginRequestDTO.getEmail());

        // 4️⃣ Devolvemos la respuesta
        return ResponseEntity.ok(
                new LoginResponseDTO(
                        token,
                        usuario.getIdUsuario(),
                        usuario.getEmail(),
                        usuario.getRol().name()
                )
        );
    }
}
