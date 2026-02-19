package com.monetics.moneticsback.repository;

import com.monetics.moneticsback.model.AuditoriaGasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository para acceder a la auditoría de los gastos corporativos.
 *
 * Este repository se utiliza para:
 * - Consultar el historial de cambios de un gasto
 * - Saber quién aprobó o rechazó un gasto
 * - Mostrar trazabilidad completa en el sistema Monetics
 *
 * NO contiene lógica de negocio, solo acceso a datos.
 */
public interface AuditoriaGastoRepository extends JpaRepository<AuditoriaGasto, Long> {

    /**
     * Obtiene todas las auditorías asociadas a un gasto concreto.
     *
     * Spring Data navega por la relación:
     * AuditoriaGasto -> gasto -> idGasto
     *
     * @param idGasto identificador del gasto
     * @return lista de auditorías del gasto
     */
    List<AuditoriaGasto> findByGasto_IdGasto(Long idGasto);

    List<AuditoriaGasto> findByGasto_IdGastoOrderByFechaCambioAsc(Long idGasto);
}
