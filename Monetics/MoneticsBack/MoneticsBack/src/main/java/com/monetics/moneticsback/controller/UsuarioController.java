package com.monetics.moneticsback.controller;

import com.monetics.moneticsback.dto.UsuarioDTO;
import com.monetics.moneticsback.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operaciones relacionadas con usuarios.
 *
 * Versión pulida:
 * - No expone entidades JPA
 * - No contiene lógica de negocio
 * - Trabaja exclusivamente con DTOs
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Obtiene un usuario por su identificador.
     *
     * @param idUsuario identificador del usuario
     * @return UsuarioDTO
     */
    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorId(
            @PathVariable Long idUsuario
    ) {
        return ResponseEntity.ok(
                usuarioService.obtenerUsuarioDTO(idUsuario)
        );
    }

    /**
     * Obtiene los empleados de un manager.
     *
     * @param idManager identificador del manager
     * @return lista de UsuarioDTO
     */
    @GetMapping("/manager/{idManager}/empleados")
    public ResponseEntity<List<UsuarioDTO>> obtenerEmpleadosDeManager(
            @PathVariable Long idManager
    ) {
        return ResponseEntity.ok(
                usuarioService.obtenerEmpleadosDeManager(idManager)
        );
    }
}
