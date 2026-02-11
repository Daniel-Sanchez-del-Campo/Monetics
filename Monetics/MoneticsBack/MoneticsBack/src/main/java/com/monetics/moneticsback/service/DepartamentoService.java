package com.monetics.moneticsback.service;

import com.monetics.moneticsback.model.Departamento;
import com.monetics.moneticsback.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;

import com.monetics.moneticsback.exception.RecursoNoEncontradoException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de la lógica relacionada con los departamentos.
 *
 * Este service actúa como capa intermedia entre controllers y repository,
 * centralizando el acceso a los datos de departamentos.
 *
 * Se utiliza para:
 * - Obtener departamentos
 * - Dar soporte al dashboard
 * - Ser utilizado por otros services (gastos, presupuestos)
 */
@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    // Inyección por constructor (buena práctica)
    public DepartamentoService(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    /**
     * Obtiene todos los departamentos de la empresa.
     *
     * Útil para:
     * - Listados
     * - Dashboards
     * - Formularios (selección de departamento)
     *
     * @return lista de departamentos
     */
    public List<Departamento> obtenerTodos() {
        return departamentoRepository.findAll();
    }

    /**
     * Obtiene un departamento por su identificador.
     *
     * @param idDepartamento identificador del departamento
     * @return Optional con el departamento si existe
     */
    public Optional<Departamento> obtenerPorId(Long idDepartamento) {
        return departamentoRepository.findById(idDepartamento);
    }

    @Transactional
    public Departamento actualizarPresupuesto(Long idDepartamento, BigDecimal presupuestoMensual, BigDecimal presupuestoAnual) {
        Departamento depto = departamentoRepository.findById(idDepartamento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado"));
        depto.setPresupuestoMensual(presupuestoMensual);
        depto.setPresupuestoAnual(presupuestoAnual);
        return departamentoRepository.save(depto);
    }
}
