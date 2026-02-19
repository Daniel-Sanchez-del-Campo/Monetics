package com.monetics.moneticsback.dto;

/**
 * ========================== LOGIN RESPONSE DTO ==========================
 * DTO de SALIDA del login y registro. Es lo que el backend devuelve al frontend
 * cuando la autenticación es exitosa.
 *
 * CONCEPTOS CLAVE PARA LA PRESENTACIÓN:
 *
 * ¿QUÉ CONTIENE?
 * 1) token   → el JWT (JSON Web Token) generado por JwtUtil.
 *    - El frontend lo almacena (típicamente en localStorage).
 *    - En cada petición futura, el frontend lo envía en el header:
 *      "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
 *
 * 2) usuario → un UsuarioDTO con los datos básicos del usuario autenticado:
 *    - idUsuario, nombre, email, rol
 *    - NUNCA se incluye la contraseña en la respuesta.
 *    - El frontend usa estos datos para mostrar el nombre del usuario,
 *      personalizar la interfaz según el rol, etc.
 *
 * ¿POR QUÉ DEVOLVEMOS EL USUARIO JUNTO CON EL TOKEN?
 * - Para que el frontend tenga los datos del usuario en UNA SOLA petición.
 * - Sin esto, el frontend tendría que hacer un segundo request (GET /api/usuarios/me)
 *   para saber quién es el usuario logueado.
 *
 * EJEMPLO DE RESPUESTA JSON:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2wiOlt7...",
 *   "usuario": {
 *     "idUsuario": 1,
 *     "nombre": "Juan García",
 *     "email": "juan@monetics.com",
 *     "rol": "ROLE_USER"
 *   }
 * }
 * ======================================================================
 */
public class LoginResponseDTO {

    // Token JWT que el frontend debe almacenar y enviar en cada petición
    private String token;

    // Datos básicos del usuario autenticado (sin contraseña)
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
