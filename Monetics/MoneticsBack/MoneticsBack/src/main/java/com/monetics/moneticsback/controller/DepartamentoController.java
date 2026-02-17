package com.monetics.moneticsback.controller;

import com.monetics.moneticsback.model.Departamento;
import com.monetics.moneticsback.service.DepartamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public ResponseEntity<List<Departamento>> obtenerTodos() {
        return ResponseEntity.ok(departamentoService.obtenerTodos());
    }

    @PutMapping("/{id}/presupuesto")
    public ResponseEntity<Departamento> actualizarPresupuesto(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> body
    ) {
        Departamento depto = departamentoService.actualizarPresupuesto(
                id,
                body.get("presupuestoMensual"),
                body.get("presupuestoAnual")
        );
        return ResponseEntity.ok(depto);
    }
}
