package com.monetics.moneticsback.dto;

/**
 * DTO de salida del login.
 *
 * Contiene:
 * - Token JWT
 * - Información del usuario autenticado como UsuarioDTO anidado
 */
public class LoginResponseDTO {

    private String token;
    private UsuarioDTO usuario;

    public LoginResponseDTO(String token, UsuarioDTO usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }
}
