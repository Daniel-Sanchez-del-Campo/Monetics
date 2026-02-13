package com.monetics.moneticsback.dto;

public class CategoriaDTO {

    private Long idCategoria;
    private String nombre;
    private String descripcion;
    private String color;
    private Boolean activa;

    public CategoriaDTO() {}

    public CategoriaDTO(Long idCategoria, String nombre, String descripcion, String color, Boolean activa) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.color = color;
        this.activa = activa;
    }

    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
}
