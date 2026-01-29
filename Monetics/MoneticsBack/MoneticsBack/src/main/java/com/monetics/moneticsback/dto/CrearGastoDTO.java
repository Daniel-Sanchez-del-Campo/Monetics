package com.monetics.moneticsback.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO utilizado para la creación de un gasto desde el frontend.
 *
 * Contiene únicamente los datos que el usuario puede introducir.
 * El resto de campos (estado, fechas, auditoría) se gestionan
 * en el backend.
 */
public class CrearGastoDTO {

    private String descripcion;
    private BigDecimal importeOriginal;
    private String monedaOriginal;
    private LocalDate fechaGasto;
    private Long idDepartamento;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getImporteOriginal() {
        return importeOriginal;
    }

    public void setImporteOriginal(BigDecimal importeOriginal) {
        this.importeOriginal = importeOriginal;
    }

    public String getMonedaOriginal() {
        return monedaOriginal;
    }

    public void setMonedaOriginal(String monedaOriginal) {
        this.monedaOriginal = monedaOriginal;
    }

    public LocalDate getFechaGasto() {
        return fechaGasto;
    }

    public void setFechaGasto(LocalDate fechaGasto) {
        this.fechaGasto = fechaGasto;
    }

    public Long getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Long idDepartamento) {
        this.idDepartamento = idDepartamento;
    }
}
