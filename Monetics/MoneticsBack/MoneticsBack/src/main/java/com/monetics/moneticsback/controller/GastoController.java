package com.monetics.moneticsback.controller;

import com.monetics.moneticsback.dto.FiltroGastoDTO;
import com.monetics.moneticsback.dto.GastoDTO;
import com.monetics.moneticsback.dto.CrearGastoDTO;
import com.monetics.moneticsback.service.GastoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
/**
 * Controller REST encargado de los endpoints de gastos.
 *
 * Este controller:
 * - No contiene lógica de negocio
 * - Trabaja exclusivamente con DTOs
 * - Delegada todo en el service
 */

@RestController
@RequestMapping("/api/gastos")
public class GastoController {

    private final GastoService gastoService;

    public GastoController(GastoService gastoService) {
        this.gastoService = gastoService;
    }

    @GetMapping
    public ResponseEntity<List<GastoDTO>> obtenerTodosLosGastos() {
        return ResponseEntity.ok(gastoService.obtenerTodosLosGastos());
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<GastoDTO>> filtrarGastos(
            @RequestParam Long idUsuario,
            @RequestParam String rol,
            @RequestParam(required = false) String estadoGasto,
            @RequestParam(required = false) Long idDepartamento,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) BigDecimal importeMin,
            @RequestParam(required = false) BigDecimal importeMax,
            @RequestParam(required = false) String texto
    ) {
        FiltroGastoDTO filtro = new FiltroGastoDTO();
        filtro.setEstadoGasto(estadoGasto);
        filtro.setIdDepartamento(idDepartamento);
        filtro.setIdCategoria(idCategoria);
        filtro.setFechaDesde(fechaDesde);
        filtro.setFechaHasta(fechaHasta);
        filtro.setImporteMin(importeMin);
        filtro.setImporteMax(importeMax);
        filtro.setTexto(texto);
        return ResponseEntity.ok(gastoService.buscarGastosFiltrados(filtro, idUsuario, rol));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<GastoDTO>> obtenerGastosPorUsuario(
            @PathVariable Long idUsuario
    ) {
        return ResponseEntity.ok(
                gastoService.obtenerGastosPorUsuario(idUsuario)
        );
    }

    @GetMapping("/manager/{idManager}")
    public ResponseEntity<List<GastoDTO>> obtenerGastosDelEquipo(
            @PathVariable Long idManager
    ) {
        return ResponseEntity.ok(
                gastoService.obtenerGastosDelEquipo(idManager)
        );
    }

    @PostMapping("/usuario/{idUsuario}")
    public ResponseEntity<GastoDTO> crearGasto(
            @PathVariable Long idUsuario,
            @RequestBody CrearGastoDTO dto
    ) {
        return new ResponseEntity<>(
                gastoService.crearGasto(dto, idUsuario),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{idGasto}")
    public ResponseEntity<Void> eliminarGasto(@PathVariable Long idGasto) {
        gastoService.eliminarGasto(idGasto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/eliminar-batch")
    public ResponseEntity<Void> eliminarGastos(@RequestBody List<Long> ids) {
        gastoService.eliminarGastos(ids);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{idGasto}/enviar-revision")
    public ResponseEntity<Void> enviarARevision(
            @PathVariable Long idGasto,
            @RequestParam Long idUsuario
    ) {
        gastoService.enviarGastoARevision(idGasto, idUsuario);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{idGasto}/aprobar")
    public ResponseEntity<Void> aprobarGasto(
            @PathVariable Long idGasto,
            @RequestParam Long idManager
    ) {
        gastoService.aprobarGasto(idGasto, idManager);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{idGasto}/rechazar")
    public ResponseEntity<Void> rechazarGasto(
            @PathVariable Long idGasto,
            @RequestParam Long idManager,
            @RequestBody Map<String, String> body
    ) {
        gastoService.rechazarGasto(idGasto, idManager, body.get("comentario"));
        return ResponseEntity.ok().build();
    }
}
