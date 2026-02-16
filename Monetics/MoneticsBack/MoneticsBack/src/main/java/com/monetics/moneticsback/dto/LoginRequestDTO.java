package com.monetics.moneticsback.dto;

/**
 * ========================== LOGIN REQUEST DTO ==========================
 * DTO (Data Transfer Object) de ENTRADA para el endpoint de login.
 *
 * CONCEPTOS CLAVE PARA LA PRESENTACIÓN:
 *
 * ¿QUÉ ES UN DTO?
 * - Es un objeto que transporta datos entre capas (frontend ↔ backend).
 * - NO es una entidad de BD, es solo un "envoltorio" de datos.
 * - Evita exponer la estructura interna de nuestras entidades.
 *
 * ¿CÓMO LLEGAN LOS DATOS?
 * - El frontend Angular envía un JSON en el body de la petición:
 *   POST /api/auth/login
 *   Body: { "email": "usuario@email.com", "password": "miContraseña" }
 *
 * - Spring deserializa automáticamente ese JSON a este DTO gracias
 *   a la anotación @RequestBody en AuthController.
 *
 * SEGURIDAD:
 * - La contraseña viaja en TEXTO PLANO en el body del request.
 * - Esto es seguro SIEMPRE QUE se use HTTPS (cifra toda la comunicación).
 * - Una vez recibida, la contraseña se compara con BCrypt y NUNCA se almacena
 *   en texto plano ni se devuelve en la respuesta.
 * ======================================================================
 */
public class LoginRequestDTO {

    // Email del usuario → se usa como "username" en Spring Security
    private String email;

    // Contraseña en texto plano → se compara contra el hash BCrypt de la BD
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
