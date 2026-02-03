package com.monetics.moneticsback.dto;

/**
 * DTO de entrada para el login.
 *
 * Contiene las credenciales que el usuario introduce
 * en el formulario de login.
 */
public class LoginRequestDTO {

    private String email;
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
