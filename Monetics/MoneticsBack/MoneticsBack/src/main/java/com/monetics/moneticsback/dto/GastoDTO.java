package com.monetics.moneticsback.dto;

import com.monetics.moneticsback.model.enums.EstadoGasto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO que representa un gasto corporativo de cara al frontend.
 *
 * Este DTO se utiliza para:
 * - Enviar información de gastos al cliente
 * - Evitar exponer directamente la entidad JPA
 * - Controlar exactamente qué datos se muestran
 *
 * No contiene lógica de negocio.
 */
public class GastoDTO {

    private Long idGasto;
    private String descripcion;
    private BigDecimal importeOriginal;
    private String monedaOriginal;
    private BigDecimal importeEur;
    private EstadoGasto estadoGasto;
    private LocalDate fechaGasto;
    private String nombreDepartamento;
    private String imagenTicket;

    // Getters y setters (sin Lombok a propósito para claridad en DTOs)

    public Long getIdGasto() {
        return idGasto;
    }

    public void setIdGasto(Long idGasto) {
        this.idGasto = idGasto;
    }

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

    public BigDecimal getImporteEur() {
        return importeEur;
    }

    public void setImporteEur(BigDecimal importeEur) {
        this.importeEur = importeEur;
    }

    public EstadoGasto getEstadoGasto() {
        return estadoGasto;
    }

    public void setEstadoGasto(EstadoGasto estadoGasto) {
        this.estadoGasto = estadoGasto;
    }

    public LocalDate getFechaGasto() {
        return fechaGasto;
    }

    public void setFechaGasto(LocalDate fechaGasto) {
        this.fechaGasto = fechaGasto;
    }

    public String getNombreDepartamento() {
        return nombreDepartamento;
    }

    public void setNombreDepartamento(String nombreDepartamento) {
        this.nombreDepartamento = nombreDepartamento;
    }

    public String getImagenTicket() {
        return imagenTicket;
    }

    public void setImagenTicket(String imagenTicket) {
        this.imagenTicket = imagenTicket;
    }
}
