package com.monetics.moneticsback.dto;

public class CrearCategoriaDTO {

    private String nombre;
    private String descripcion;
    private String color;

    public CrearCategoriaDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
