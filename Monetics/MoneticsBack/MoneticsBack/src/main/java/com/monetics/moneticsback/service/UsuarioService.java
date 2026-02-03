package com.monetics.moneticsback.service;

import com.monetics.moneticsback.dto.CrearUsuarioDTO;
import com.monetics.moneticsback.dto.UsuarioDTO;
import com.monetics.moneticsback.exception.RecursoNoEncontradoException;
import com.monetics.moneticsback.model.Departamento;
import com.monetics.moneticsback.model.Usuario;
import com.monetics.moneticsback.model.enums.RolUsuario;
import com.monetics.moneticsback.repository.DepartamentoRepository;
import com.monetics.moneticsback.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de la lógica relacionada con los usuarios del sistema.
 *
 * IMPORTANTE:
 * - Los controllers SOLO deben usar métodos que devuelven DTOs
 * - Los services y Security usan métodos que devuelven ENTIDADES
 *
 * Esto mantiene una arquitectura limpia y segura.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final DepartamentoRepository departamentoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            DepartamentoRepository departamentoRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.departamentoRepository = departamentoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ============================
       CREACIÓN DE USUARIO
       ============================ */

    /**
     * Crea un usuario nuevo de forma segura.
     *
     * - Hashea la contraseña con BCrypt
     * - Guarda SOLO el hash
     * - Evita problemas posteriores en el login
     * - Devuelve el usuario creado
     */
    public Usuario crearUsuario(CrearUsuarioDTO dto) {

        Usuario usuario = new Usuario();

        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());

        // 🔐 AQUÍ SE HASHEA LA CONTRASEÑA
        usuario.setPassword(
                passwordEncoder.encode(dto.getPassword())
        );

        // Asignar rol (por defecto ROLE_USER si no se especifica)
        if (dto.getRol() != null && !dto.getRol().isEmpty()) {
            usuario.setRol(RolUsuario.valueOf(dto.getRol()));
        } else {
            usuario.setRol(RolUsuario.ROLE_USER);
        }

        // Asignar departamento si se especifica
        if (dto.getIdDepartamento() != null) {
            Departamento departamento = departamentoRepository.findById(dto.getIdDepartamento())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado"));
            usuario.setDepartamento(departamento);
        }

        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }

    /* ============================
       MÉTODOS PARA CONTROLLERS (DTO)
       ============================ */

    /**
     * Obtiene un usuario por ID y lo devuelve como DTO.
     */
    public UsuarioDTO obtenerUsuarioDTO(Long idUsuario) {
        Usuario usuario = obtenerUsuarioEntidad(idUsuario);
        return mapearAUsuarioDTO(usuario);
    }

    /**
     * Obtiene los empleados de un manager como DTOs.
     */
    public List<UsuarioDTO> obtenerEmpleadosDeManager(Long idManager) {
        return usuarioRepository.findByManager_IdUsuario(idManager)
                .stream()
                .map(this::mapearAUsuarioDTO)
                .collect(Collectors.toList());
    }

    /* ============================
       VALIDACIONES
       ============================ */

    /**
     * Verifica si un email ya está registrado en el sistema.
     */
    public boolean existeEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    /* ============================
       MÉTODOS PARA SERVICES / SECURITY (ENTIDAD)
       ============================ */

    /**
     * Obtiene un usuario como entidad a partir de su ID.
     *
     * Uso interno (GastoService, Security, etc.).
     */
    public Usuario obtenerUsuarioEntidad(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Usuario no encontrado")
                );
    }

    /**
     * Obtiene un usuario como entidad a partir de su EMAIL.
     *
     * Uso interno:
     * - Login
     * - JWT
     * - Spring Security
     */
    public Usuario obtenerUsuarioEntidadPorEmail(String email) {
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
