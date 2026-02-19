package com.monetics.moneticsback.controller;

import com.monetics.moneticsback.dto.AuditoriaGastoDTO;
import com.monetics.moneticsback.service.AuditoriaGastoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditorias")
public class AuditoriaGastoController {

    private final AuditoriaGastoService auditoriaGastoService;

    public AuditoriaGastoController(AuditoriaGastoService auditoriaGastoService) {
        this.auditoriaGastoService = auditoriaGastoService;
    }

    @GetMapping("/gasto/{idGasto}")
    public ResponseEntity<List<AuditoriaGastoDTO>> obtenerHistorial(@PathVariable Long idGasto) {
        return ResponseEntity.ok(auditoriaGastoService.obtenerHistorialDeGasto(idGasto));
    }
}
