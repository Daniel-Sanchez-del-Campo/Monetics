package com.monetics.moneticsback.service;

import com.monetics.moneticsback.model.Presupuesto;
import com.monetics.moneticsback.model.enums.TipoPeriodo;
import com.monetics.moneticsback.repository.PresupuestoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio encargado de la lógica relacionada con los presupuestos.
 *
 * Centraliza el acceso y validación de presupuestos por:
 * - departamento
 * - tipo de periodo (mensual / anual)
 *
 * Este service será utilizado por GastoService para:
 * - validar límites de gasto
 * - generar alertas de presupuesto excedido
 */
@Service
public class PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;

    public PresupuestoService(PresupuestoRepository presupuestoRepository) {
        this.presupuestoRepository = presupuestoRepository;
    }

    /**
     * Obtiene un presupuesto de un departamento para un periodo concreto.
     *
     * @param idDepartamento identificador del departamento
     * @param tipoPeriodo tipo de periodo (MENSUAL / ANUAL)
     * @return Optional con el presupuesto si existe
     */
    public Optional<Presupuesto> obtenerPresupuesto(
            Long idDepartamento,
            TipoPeriodo tipoPeriodo
    ) {
        return presupuestoRepository
                .findByDepartamento_IdDepartamentoAndTipoPeriodo(
                        idDepartamento,
                        tipoPeriodo
                );
    }
}
