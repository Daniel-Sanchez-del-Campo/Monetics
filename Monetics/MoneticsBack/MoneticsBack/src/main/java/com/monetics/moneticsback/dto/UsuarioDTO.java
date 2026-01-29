package com.monetics.moneticsback.dto;

/**
 * DTO que representa un usuario de cara al frontend.
 *
 * Se utiliza para:
 * - Mostrar información básica del usuario
 * - Evitar exponer campos sensibles como password
 */
public class UsuarioDTO {

    private Long idUsuario;
    private String nombre;
    private String email;
    private String rol;

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
