package com.monetics.moneticsback.dto;

import com.monetics.moneticsback.model.enums.EstadoGasto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GastoDTO {

    private Long idGasto;
    private String descripcion;
    private BigDecimal importeOriginal;
    private String monedaOriginal;
    private BigDecimal importeEur;
    private EstadoGasto estadoGasto;
    private LocalDate fechaGasto;
    private String nombreDepartamento;
    private Long idCategoria;
    private String nombreCategoria;
    private String colorCategoria;

    // Nuevos campos de Drive (sustituyen a imagenTicket)
    private String driveFileId;
    private String driveFileUrl;
    private String imagenNombre;

    // Campos de IA
    private Boolean analizadoPorIa;
    private BigDecimal iaConfianza;

    // --- Getters y Setters ---

    public Long getIdGasto() { return idGasto; }
    public void setIdGasto(Long idGasto) { this.idGasto = idGasto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getImporteOriginal() { return importeOriginal; }
    public void setImporteOriginal(BigDecimal importeOriginal) { this.importeOriginal = importeOriginal; }

    public String getMonedaOriginal() { return monedaOriginal; }
    public void setMonedaOriginal(String monedaOriginal) { this.monedaOriginal = monedaOriginal; }

    public BigDecimal getImporteEur() { return importeEur; }
    public void setImporteEur(BigDecimal importeEur) { this.importeEur = importeEur; }

    public EstadoGasto getEstadoGasto() { return estadoGasto; }
    public void setEstadoGasto(EstadoGasto estadoGasto) { this.estadoGasto = estadoGasto; }

    public LocalDate getFechaGasto() { return fechaGasto; }
    public void setFechaGasto(LocalDate fechaGasto) { this.fechaGasto = fechaGasto; }

    public String getNombreDepartamento() { return nombreDepartamento; }
    public void setNombreDepartamento(String nombreDepartamento) { this.nombreDepartamento = nombreDepartamento; }

    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }

    public String getColorCategoria() { return colorCategoria; }
    public void setColorCategoria(String colorCategoria) { this.colorCategoria = colorCategoria; }

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
