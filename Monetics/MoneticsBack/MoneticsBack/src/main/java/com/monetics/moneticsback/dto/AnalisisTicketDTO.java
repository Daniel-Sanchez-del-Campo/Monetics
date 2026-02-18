package com.monetics.moneticsback.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO con los datos extraidos por la IA al analizar un ticket.
 */
public class AnalisisTicketDTO {

    private String descripcion;
    private BigDecimal importeOriginal;
    private String monedaOriginal;
    private String fechaGasto;
    private String categoriaSugerida;
    private Long idCategoriaSugerida;
    private BigDecimal confianza;
    private Map<String, BigDecimal> confianzaPorCampo;

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getImporteOriginal() { return importeOriginal; }
    public void setImporteOriginal(BigDecimal importeOriginal) { this.importeOriginal = importeOriginal; }

    public String getMonedaOriginal() { return monedaOriginal; }
    public void setMonedaOriginal(String monedaOriginal) { this.monedaOriginal = monedaOriginal; }

    public String getFechaGasto() { return fechaGasto; }
    public void setFechaGasto(String fechaGasto) { this.fechaGasto = fechaGasto; }

    public String getCategoriaSugerida() { return categoriaSugerida; }
    public void setCategoriaSugerida(String categoriaSugerida) { this.categoriaSugerida = categoriaSugerida; }

    public Long getIdCategoriaSugerida() { return idCategoriaSugerida; }
    public void setIdCategoriaSugerida(Long idCategoriaSugerida) { this.idCategoriaSugerida = idCategoriaSugerida; }

    public BigDecimal getConfianza() { return confianza; }
    public void setConfianza(BigDecimal confianza) { this.confianza = confianza; }

    public Map<String, BigDecimal> getConfianzaPorCampo() { return confianzaPorCampo; }
    public void setConfianzaPorCampo(Map<String, BigDecimal> confianzaPorCampo) { this.confianzaPorCampo = confianzaPorCampo; }
}
