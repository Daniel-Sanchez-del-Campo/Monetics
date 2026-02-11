package com.monetics.moneticsback.service;

import com.monetics.moneticsback.dto.DashboardDTO;
import com.monetics.moneticsback.dto.DashboardDTO.AlertaPresupuestoDTO;
import com.monetics.moneticsback.dto.DashboardDTO.GastoPorDepartamentoDTO;
import com.monetics.moneticsback.model.Departamento;
import com.monetics.moneticsback.model.Gasto;
import com.monetics.moneticsback.model.enums.EstadoGasto;
import com.monetics.moneticsback.repository.DepartamentoRepository;
import com.monetics.moneticsback.repository.GastoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final GastoRepository gastoRepository;
    private final DepartamentoRepository departamentoRepository;

    public DashboardService(GastoRepository gastoRepository, DepartamentoRepository departamentoRepository) {
        this.gastoRepository = gastoRepository;
        this.departamentoRepository = departamentoRepository;
    }

    public DashboardDTO obtenerDashboard() {
        DashboardDTO dto = new DashboardDTO();
        List<Gasto> todosGastos = gastoRepository.findAll();
        List<Departamento> departamentos = departamentoRepository.findAll();

        // === KPIs globales ===
        dto.setTotalGastos(todosGastos.size());

        BigDecimal pendienteReembolso = todosGastos.stream()
                .filter(g -> g.getEstadoGasto() == EstadoGasto.APROBADO)
                .map(Gasto::getImporteEur)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalPendienteReembolso(pendienteReembolso);

        BigDecimal totalAprobado = todosGastos.stream()
                .filter(g -> g.getEstadoGasto() == EstadoGasto.APROBADO)
                .map(Gasto::getImporteEur)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalAprobado(totalAprobado);

        // Gastos del mes actual
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        BigDecimal totalMes = todosGastos.stream()
                .filter(g -> !g.getFechaGasto().isBefore(inicioMes))
                .map(Gasto::getImporteEur)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalGastosMes(totalMes);

        dto.setGastosPendientes((int) todosGastos.stream()
                .filter(g -> g.getEstadoGasto() == EstadoGasto.PENDIENTE_APROBACION)
                .count());
        dto.setGastosAprobados((int) todosGastos.stream()
                .filter(g -> g.getEstadoGasto() == EstadoGasto.APROBADO)
                .count());
        dto.setGastosRechazados((int) todosGastos.stream()
                .filter(g -> g.getEstadoGasto() == EstadoGasto.RECHAZADO)
                .count());

        // === Gastos por departamento ===
        List<GastoPorDepartamentoDTO> gastosPorDepto = new ArrayList<>();
        List<AlertaPresupuestoDTO> alertas = new ArrayList<>();

        for (Departamento depto : departamentos) {
            List<Gasto> gastosDept = todosGastos.stream()
                    .filter(g -> g.getDepartamento().getIdDepartamento().equals(depto.getIdDepartamento()))
                    .toList();

            BigDecimal totalDept = gastosDept.stream()
                    .map(Gasto::getImporteEur)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            gastosPorDepto.add(new GastoPorDepartamentoDTO(
                    depto.getNombre(),
                    totalDept,
                    depto.getPresupuestoMensual(),
                    gastosDept.size()
            ));

            // Alerta si se supera el 80% del presupuesto mensual
            if (depto.getPresupuestoMensual().compareTo(BigDecimal.ZERO) > 0) {
                // Solo gastos del mes actual para la alerta
                BigDecimal gastoMesDept = gastosDept.stream()
                        .filter(g -> !g.getFechaGasto().isBefore(inicioMes))
                        .map(Gasto::getImporteEur)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                double porcentaje = gastoMesDept
                        .divide(depto.getPresupuestoMensual(), 4, RoundingMode.HALF_UP)
                        .doubleValue() * 100;

                if (porcentaje >= 80) {
                    alertas.add(new AlertaPresupuestoDTO(
                            depto.getNombre(),
                            depto.getPresupuestoMensual(),
                            gastoMesDept,
                            porcentaje
                    ));
                }
            }
        }

        dto.setGastosPorDepartamento(gastosPorDepto);
        dto.setAlertasPresupuesto(alertas);

        return dto;
    }
}
