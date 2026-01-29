package com.monetics.moneticsback.repository;

import com.monetics.moneticsback.model.Presupuesto;
import com.monetics.moneticsback.model.enums.TipoPeriodo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository para la gestión de presupuestos.
 *
 * Permite consultar presupuestos según:
 * - departamento
 * - tipo de periodo (mensual / anual)
 */
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {

    // Obtener presupuesto de un departamento para un periodo concreto
    Optional<Presupuesto> findByDepartamento_IdDepartamentoAndTipoPeriodo(
            Long idDepartamento,
            TipoPeriodo tipoPeriodo
    );
}
