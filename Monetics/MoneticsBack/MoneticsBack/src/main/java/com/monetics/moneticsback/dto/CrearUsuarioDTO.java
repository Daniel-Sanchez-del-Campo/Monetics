package com.monetics.moneticsback.dto;

/**
 * DTO para crear un usuario.
 *
 * La contraseña llega en texto plano y se hashea en el service.
 */
public class CrearUsuarioDTO {

    private String nombre;
    private String email;
    private String password;
    private String rol;

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

    public String getPassword() {
        return password;
    }

    //NUNCA devolver la password
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
