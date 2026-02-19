package com.monetics.moneticsback.controller;

import com.monetics.moneticsback.dto.AdminUsuarioDTO;
import com.monetics.moneticsback.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UsuarioService usuarioService;

    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<AdminUsuarioDTO>> obtenerTodosLosUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuariosAdmin());
    }

    @PutMapping("/usuarios/{idUsuario}")
    public ResponseEntity<AdminUsuarioDTO> actualizarUsuario(
            @PathVariable Long idUsuario,
            @RequestBody Map<String, Object> body
    ) {
        String rol = body.containsKey("rol") ? (String) body.get("rol") : null;
        Boolean activo = body.containsKey("activo") ? (Boolean) body.get("activo") : null;
        return ResponseEntity.ok(usuarioService.actualizarUsuarioAdmin(idUsuario, rol, activo));
    }
}
