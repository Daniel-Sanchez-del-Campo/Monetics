package com.monetics.moneticsback.service;

import com.monetics.moneticsback.dto.AuditoriaGastoDTO;
import com.monetics.moneticsback.model.AuditoriaGasto;
import com.monetics.moneticsback.model.Gasto;
import com.monetics.moneticsback.model.Usuario;
import com.monetics.moneticsback.repository.AuditoriaGastoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de gestionar la auditoría de los gastos corporativos.
 *
 * Centraliza toda la lógica relacionada con:
 * - Registro de cambios de estado de un gasto
 * - Consulta del historial de auditorías
 *
 * De esta forma, evitamos duplicar código en otros services
 * y garantizamos la trazabilidad completa del sistema Monetics.
 */
@Service
public class AuditoriaGastoService {

    private final AuditoriaGastoRepository auditoriaGastoRepository;

    public AuditoriaGastoService(AuditoriaGastoRepository auditoriaGastoRepository) {
        this.auditoriaGastoRepository = auditoriaGastoRepository;
    }

    /**
     * Registra un cambio de estado de un gasto en la auditoría.
     *
     * Este método se llamará desde GastoService cada vez que:
     * - un gasto se envía a aprobación
     * - un gasto se aprueba
     * - un gasto se rechaza
     *
     * @param gasto gasto afectado
     * @param estadoAnterior estado previo
     * @param estadoNuevo nuevo estado
     * @param usuario usuario que realiza la acción
     * @param comentario comentario opcional
     */
    public void registrarCambioEstado(
            Gasto gasto,
            String estadoAnterior,
            String estadoNuevo,
            Usuario usuario,
            String comentario
    ) {
        AuditoriaGasto auditoria = new AuditoriaGasto();

        auditoria.setGasto(gasto);
        auditoria.setEstadoAnterior(estadoAnterior);
        auditoria.setEstadoNuevo(estadoNuevo);
        auditoria.setUsuarioAccion(usuario);
        auditoria.setComentario(comentario);
        auditoria.setFechaCambio(LocalDateTime.now());

        auditoriaGastoRepository.save(auditoria);
    }

    /**
     * Obtiene el historial de auditorías de un gasto concreto.
     *
     * @param idGasto identificador del gasto
     * @return lista de auditorías
     */
    public List<AuditoriaGasto> obtenerAuditoriasDeGasto(Long idGasto) {
        return auditoriaGastoRepository.findByGasto_IdGasto(idGasto);
    }

    public List<AuditoriaGastoDTO> obtenerHistorialDeGasto(Long idGasto) {
        return auditoriaGastoRepository.findByGasto_IdGastoOrderByFechaCambioAsc(idGasto)
                .stream()
                .map(this::mapearAAuditoriaDTO)
                .collect(Collectors.toList());
    }

    private AuditoriaGastoDTO mapearAAuditoriaDTO(AuditoriaGasto a) {
        AuditoriaGastoDTO dto = new AuditoriaGastoDTO();
        dto.setIdAuditoria(a.getIdAuditoria());
        dto.setEstadoAnterior(a.getEstadoAnterior());
        dto.setEstadoNuevo(a.getEstadoNuevo());
        dto.setFechaCambio(a.getFechaCambio());
        dto.setComentario(a.getComentario());
        dto.setNombreUsuarioAccion(a.getUsuarioAccion().getNombre());
        return dto;
    }
}
