package com.monetics.moneticsback.service;

import com.monetics.moneticsback.dto.UsuarioDTO;
import com.monetics.moneticsback.exception.RecursoNoEncontradoException;
import com.monetics.moneticsback.model.Usuario;
import com.monetics.moneticsback.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de la lógica relacionada con los usuarios del sistema.
 *
 * IMPORTANTE:
 * - Los controllers SOLO deben usar métodos que devuelven DTOs
 * - Los services pueden usar métodos que devuelven entidades
 *
 * Esto evita exponer entidades JPA al exterior y mantiene
 * una arquitectura limpia.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /* ============================
       MÉTODOS PARA CONTROLLERS (DTO)
       ============================ */

    /**
     * Obtiene un usuario y lo devuelve como DTO.
     *
     * Uso exclusivo desde controllers.
     */
    public UsuarioDTO obtenerUsuarioDTO(Long idUsuario) {
        Usuario usuario = obtenerUsuarioEntidad(idUsuario);
        return mapearAUsuarioDTO(usuario);
    }

    /**
     * Obtiene los empleados de un manager como DTOs.
     *
     * Uso desde controllers.
     */
    public List<UsuarioDTO> obtenerEmpleadosDeManager(Long idManager) {
        return usuarioRepository.findByManager_IdUsuario(idManager)
                .stream()
                .map(this::mapearAUsuarioDTO)
                .collect(Collectors.toList());
    }

    /* ============================
       MÉTODOS PARA SERVICES (ENTIDAD)
       ============================ */

    /**
     * Obtiene un usuario como entidad JPA.
     *
     * Uso EXCLUSIVO desde otros services (GastoService, Security, etc.).
     */
    public Usuario obtenerUsuarioEntidad(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Usuario no encontrado")
                );
    }

    /**
     * Obtiene un usuario por email como entidad.
     *
     * Uso interno (autenticación, seguridad).
     */
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Usuario no encontrado")
                );
    }

    /* ============================
       MAPEADOR
       ============================ */

    /**
     * Convierte una entidad Usuario en UsuarioDTO.
     *
     * Evita exponer:
     * - password
     * - relaciones JPA
     */
    private UsuarioDTO mapearAUsuarioDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();

        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol().name());

        return dto;
    }
}
