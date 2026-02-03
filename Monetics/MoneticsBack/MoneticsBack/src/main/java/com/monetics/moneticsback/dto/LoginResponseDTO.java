package com.monetics.moneticsback.dto;

/**
 * DTO de salida del login.
 *
 * Contiene:
 * - Token JWT
 * - Información básica del usuario autenticado
 */
public class LoginResponseDTO {

    private String token;
    private Long idUsuario;
    private String email;
    private String rol;

    public LoginResponseDTO(
            String token,
            Long idUsuario,
            String email,
            String rol
    ) {
        this.token = token;
        this.idUsuario = idUsuario;
        this.email = email;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public String getRol() {
        return rol;
    }
}
