package com.monetics.moneticsback.dto;

import java.time.LocalDateTime;

public class AuditoriaGastoDTO {

    private Long idAuditoria;
    private String estadoAnterior;
    private String estadoNuevo;
    private LocalDateTime fechaCambio;
    private String comentario;
    private String nombreUsuarioAccion;

    public Long getIdAuditoria() { return idAuditoria; }
    public void setIdAuditoria(Long idAuditoria) { this.idAuditoria = idAuditoria; }
    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }
    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public String getNombreUsuarioAccion() { return nombreUsuarioAccion; }
    public void setNombreUsuarioAccion(String nombreUsuarioAccion) { this.nombreUsuarioAccion = nombreUsuarioAccion; }
}
