package com.monetics.moneticsback.service;

import com.monetics.moneticsback.dto.DashboardDTO;
import com.monetics.moneticsback.dto.DashboardDTO.AlertaPresupuestoDTO;
import com.monetics.moneticsback.dto.DashboardDTO.GastoPorCategoriaDTO;
import com.monetics.moneticsback.dto.DashboardDTO.GastoPorDepartamentoDTO;
import com.monetics.moneticsback.model.Categoria;
import com.monetics.moneticsback.model.Departamento;
import com.monetics.moneticsback.model.Gasto;
import com.monetics.moneticsback.model.enums.EstadoGasto;
import com.monetics.moneticsback.repository.CategoriaRepository;
import com.monetics.moneticsback.repository.DepartamentoRepository;
import com.monetics.moneticsback.repository.GastoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final GastoRepository gastoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final CategoriaRepository categoriaRepository;

    public DashboardService(GastoRepository gastoRepository,
                            DepartamentoRepository departamentoRepository,
                            CategoriaRepository categoriaRepository) {
        this.gastoRepository = gastoRepository;
        this.departamentoRepository = departamentoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public DashboardDTO obtenerDashboard(Long idUsuario) {
        DashboardDTO dto = new DashboardDTO();
        List<Gasto> todosGastos = gastoRepository.findAll();
        List<Departamento> departamentos = departamentoRepository.findAll();

        // === KPIs globales ===
        dto.setTotalGastos(todosGastos.size());

        // Si se pasa idUsuario, el pendiente de reembolso es solo del usuario
        BigDecimal pendienteReembolso;
        if (idUsuario != null) {
            pendienteReembolso = todosGastos.stream()
                    .filter(g -> g.getEstadoGasto() == EstadoGasto.APROBADO)
                    .filter(g -> g.getUsuario().getIdUsuario().equals(idUsuario))
                    .map(Gasto::getImporteEur)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            pendienteReembolso = todosGastos.stream()
                    .filter(g -> g.getEstadoGasto() == EstadoGasto.APROBADO)
                    .map(Gasto::getImporteEur)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
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

        // Gastos del mes anterior y variación porcentual
        LocalDate inicioMesAnterior = inicioMes.minusMonths(1);
        BigDecimal totalMesAnterior = todosGastos.stream()
                .filter(g -> !g.getFechaGasto().isBefore(inicioMesAnterior) && g.getFechaGasto().isBefore(inicioMes))
                .map(Gasto::getImporteEur)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalGastosMesAnterior(totalMesAnterior);

        if (totalMesAnterior.compareTo(BigDecimal.ZERO) > 0) {
            double variacion = totalMes.subtract(totalMesAnterior)
                    .divide(totalMesAnterior, 4, RoundingMode.HALF_UP)
                    .doubleValue() * 100;
            dto.setVariacionMensualPorcentaje(variacion);
        } else {
            dto.setVariacionMensualPorcentaje(null);
        }

        dto.setGastosPendientes((int) todosGastos.stream()
                .filter(g -> g.getEstadoGasto() == EstadoGasto.PENDIENTE_APROBACION)
                .count());
        dto.setGastosAprobados((int) todosGastos.stream()
                .filter(g -> g.getEstadoGasto() == EstadoGasto.APROBADO)
                .count());
        dto.setGastosRechazados((int) todosGastos.stream()
                .filter(g -> g.getEstadoGasto() == EstadoGasto.RECHAZADO)
                .count());

        // === Gastos por departamento + alertas ===
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

            if (depto.getPresupuestoMensual().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal gastoMesDept = gastosDept.stream()
                        .filter(g -> !g.getFechaGasto().isBefore(inicioMes))
                        .map(Gasto::getImporteEur)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                double porcentaje = gastoMesDept
                        .divide(depto.getPresupuestoMensual(), 4, RoundingMode.HALF_UP)
                        .doubleValue() * 100;

                if (porcentaje >= 80) {
                    String nivel = porcentaje >= 100 ? "CRITICAL" : "WARNING";
                    alertas.add(new AlertaPresupuestoDTO(
                            depto.getNombre(),
                            depto.getPresupuestoMensual(),
                            gastoMesDept,
                            porcentaje,
                            nivel
                    ));
                }
            }
        }

        dto.setGastosPorDepartamento(gastosPorDepto);
        dto.setAlertasPresupuesto(alertas);

        // === Gastos por categoría ===
        List<Categoria> categoriasActivas = categoriaRepository.findByActivaTrue();
        List<GastoPorCategoriaDTO> gastosPorCat = new ArrayList<>();

        Map<Long, List<Gasto>> gastosPorCategoriaMap = todosGastos.stream()
                .filter(g -> g.getCategoria() != null)
                .collect(Collectors.groupingBy(g -> g.getCategoria().getIdCategoria()));

        for (Categoria cat : categoriasActivas) {
            List<Gasto> gastosCat = gastosPorCategoriaMap.getOrDefault(cat.getIdCategoria(), List.of());
            BigDecimal totalCat = gastosCat.stream()
                    .map(Gasto::getImporteEur)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (!gastosCat.isEmpty()) {
                gastosPorCat.add(new GastoPorCategoriaDTO(
                        cat.getNombre(),
                        cat.getColor(),
                        totalCat,
                        gastosCat.size()
                ));
            }
        }

        // Gastos sin categoría
        long sinCategoria = todosGastos.stream().filter(g -> g.getCategoria() == null).count();
        if (sinCategoria > 0) {
            BigDecimal totalSinCat = todosGastos.stream()
                    .filter(g -> g.getCategoria() == null)
                    .map(Gasto::getImporteEur)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            gastosPorCat.add(new GastoPorCategoriaDTO(
                    "Sin categoría",
                    "#9e9e9e",
                    totalSinCat,
                    (int) sinCategoria
            ));
        }

        dto.setGastosPorCategoria(gastosPorCat);

        return dto;
    }
}
