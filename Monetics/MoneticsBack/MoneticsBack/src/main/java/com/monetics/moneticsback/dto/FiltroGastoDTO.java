package com.monetics.moneticsback.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FiltroGastoDTO {

    private String estadoGasto;
    private Long idDepartamento;
    private Long idCategoria;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private BigDecimal importeMin;
    private BigDecimal importeMax;
    private String texto;

    public String getEstadoGasto() { return estadoGasto; }
    public void setEstadoGasto(String estadoGasto) { this.estadoGasto = estadoGasto; }
    public Long getIdDepartamento() { return idDepartamento; }
    public void setIdDepartamento(Long idDepartamento) { this.idDepartamento = idDepartamento; }
    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }
    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }
    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }
    public BigDecimal getImporteMin() { return importeMin; }
    public void setImporteMin(BigDecimal importeMin) { this.importeMin = importeMin; }
    public BigDecimal getImporteMax() { return importeMax; }
    public void setImporteMax(BigDecimal importeMax) { this.importeMax = importeMax; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}
