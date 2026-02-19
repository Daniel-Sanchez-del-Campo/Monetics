package com.monetics.moneticsback.repository;

import com.monetics.moneticsback.model.Gasto;
import com.monetics.moneticsback.model.enums.EstadoGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository encargado del acceso a los gastos corporativos.
 *
 * Incluye consultas personalizadas para:
 * - Filtrar por usuario
 * - Filtrar por estado
 * - Obtener gastos del equipo de un manager
 */
public interface GastoRepository extends JpaRepository<Gasto, Long>, JpaSpecificationExecutor<Gasto> {

    // Obtener todos los gastos de un usuario concreto
    List<Gasto> findByUsuario_IdUsuario(Long idUsuario);

    // Obtener gastos por estado (BORRADOR, APROBADO, etc.)
    List<Gasto> findByEstadoGasto(EstadoGasto estadoGasto);

    // Obtener gastos de un departamento
    List<Gasto> findByDepartamento_IdDepartamento(Long idDepartamento);

    /**
     * Consulta personalizada:
     * Obtiene todos los gastos de los empleados que dependen
     * de un manager concreto.
     *
     * Esta consulta es CLAVE para el rol ROLE_MANAGER.
     */
    @Query("""
        SELECT g
        FROM Gasto g
        JOIN g.usuario u
        WHERE u.manager.idUsuario = :idManager
    """)
    List<Gasto> obtenerGastosDelEquipo(@Param("idManager") Long idManager);
}
