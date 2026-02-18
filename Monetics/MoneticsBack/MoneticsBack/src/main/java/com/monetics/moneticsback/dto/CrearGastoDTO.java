package com.monetics.moneticsback.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CrearGastoDTO {

    private String descripcion;
    private BigDecimal importeOriginal;
    private String monedaOriginal;
    private LocalDate fechaGasto;
    private Long idDepartamento;
    private Long idCategoria;

    // Nuevos campos de Drive (sustituyen a imagenTicket)
    private String driveFileId;
    private String driveFileUrl;
    private String imagenNombre;

    // Campos de IA
    private Boolean analizadoPorIa;
    private BigDecimal iaConfianza;

    // --- Getters y Setters ---

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getImporteOriginal() { return importeOriginal; }
    public void setImporteOriginal(BigDecimal importeOriginal) { this.importeOriginal = importeOriginal; }

    public String getMonedaOriginal() { return monedaOriginal; }
    public void setMonedaOriginal(String monedaOriginal) { this.monedaOriginal = monedaOriginal; }

    public LocalDate getFechaGasto() { return fechaGasto; }
    public void setFechaGasto(LocalDate fechaGasto) { this.fechaGasto = fechaGasto; }

    public Long getIdDepartamento() { return idDepartamento; }
    public void setIdDepartamento(Long idDepartamento) { this.idDepartamento = idDepartamento; }

    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }

    public String getDriveFileId() { return driveFileId; }
    public void setDriveFileId(String driveFileId) { this.driveFileId = driveFileId; }

    public String getDriveFileUrl() { return driveFileUrl; }
    public void setDriveFileUrl(String driveFileUrl) { this.driveFileUrl = driveFileUrl; }

    public String getImagenNombre() { return imagenNombre; }
    public void setImagenNombre(String imagenNombre) { this.imagenNombre = imagenNombre; }

    public Boolean getAnalizadoPorIa() { return analizadoPorIa; }
    public void setAnalizadoPorIa(Boolean analizadoPorIa) { this.analizadoPorIa = analizadoPorIa; }

    public BigDecimal getIaConfianza() { return iaConfianza; }
    public void setIaConfianza(BigDecimal iaConfianza) { this.iaConfianza = iaConfianza; }
}
