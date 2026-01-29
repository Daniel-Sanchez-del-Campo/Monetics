package com.monetics.moneticsback.repository;

import com.monetics.moneticsback.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository para la gestión de departamentos.
 *
 * Normalmente se utiliza para:
 * - Consultas globales
 * - Cálculos de gasto por departamento
 * - Asociación con usuarios y gastos
 */
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
}
