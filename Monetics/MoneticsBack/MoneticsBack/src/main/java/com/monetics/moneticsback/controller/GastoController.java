package com.monetics.moneticsback.controller;

import com.monetics.moneticsback.dto.GastoDTO;
import com.monetics.moneticsback.dto.CrearGastoDTO;
import com.monetics.moneticsback.service.GastoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
}
