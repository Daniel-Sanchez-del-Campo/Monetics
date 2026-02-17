package com.monetics.moneticsback.service;

import com.monetics.moneticsback.dto.CrearGastoDTO;
import com.monetics.moneticsback.dto.FiltroGastoDTO;
import com.monetics.moneticsback.dto.GastoDTO;
import com.monetics.moneticsback.exception.AccesoNoPermitidoException;
import com.monetics.moneticsback.exception.OperacionNoPermitidaException;
import com.monetics.moneticsback.exception.RecursoNoEncontradoException;
import com.monetics.moneticsback.model.Categoria;
import com.monetics.moneticsback.model.Gasto;
import com.monetics.moneticsback.model.Usuario;
import com.monetics.moneticsback.model.enums.EstadoGasto;
import com.monetics.moneticsback.model.enums.RolUsuario;
import com.monetics.moneticsback.repository.CategoriaRepository;
import com.monetics.moneticsback.repository.GastoRepository;
import com.monetics.moneticsback.repository.specification.GastoSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de TODA la lógica de negocio relacionada con los gastos.
 *
 * IMPORTANTE:
 * - Usa UsuarioService.obtenerUsuarioEntidad()
 * - NO usa métodos eliminados
 * - Versión coherente con el UsuarioService actual
 */
@Service
public class GastoService {

    private final GastoRepository gastoRepository;
    private final UsuarioService usuarioService;
    private final AuditoriaGastoService auditoriaGastoService;
    private final ExchangeRateService exchangeRateService;
    private final CategoriaRepository categoriaRepository;

    public GastoService(
            GastoRepository gastoRepository,
            UsuarioService usuarioService,
            AuditoriaGastoService auditoriaGastoService,
            ExchangeRateService exchangeRateService,
            CategoriaRepository categoriaRepository
    ) {
        this.gastoRepository = gastoRepository;
        this.usuarioService = usuarioService;
        this.auditoriaGastoService = auditoriaGastoService;
        this.exchangeRateService = exchangeRateService;
        this.categoriaRepository = categoriaRepository;
    }

    /* ============================
       CONSULTAS
       ============================ */

    public List<GastoDTO> obtenerGastosPorUsuario(Long idUsuario) {
        return gastoRepository.findByUsuario_IdUsuario(idUsuario)
                .stream()
                .map(this::mapearAGastoDTO)
                .collect(Collectors.toList());
    }

    public List<GastoDTO> obtenerGastosDelEquipo(Long idManager) {
        return gastoRepository.obtenerGastosDelEquipo(idManager)
                .stream()
                .map(this::mapearAGastoDTO)
                .collect(Collectors.toList());
    }

    public List<GastoDTO> obtenerTodosLosGastos() {
        return gastoRepository.findAll()
                .stream()
                .map(this::mapearAGastoDTO)
                .collect(Collectors.toList());
    }

    /* ============================
       FILTRADO AVANZADO
       ============================ */

    public List<GastoDTO> buscarGastosFiltrados(FiltroGastoDTO filtro, Long idUsuario, String rol) {
        Specification<Gasto> spec = Specification.where(null);

        if ("ROLE_USER".equals(rol)) {
            spec = spec.and(GastoSpecification.conUsuario(idUsuario));
        } else if ("ROLE_MANAGER".equals(rol)) {
            spec = spec.and(GastoSpecification.delEquipo(idUsuario));
        }

        if (filtro.getEstadoGasto() != null && !filtro.getEstadoGasto().isBlank()) {
            spec = spec.and(GastoSpecification.conEstado(EstadoGasto.valueOf(filtro.getEstadoGasto())));
        }
        if (filtro.getIdDepartamento() != null) {
            spec = spec.and(GastoSpecification.conDepartamento(filtro.getIdDepartamento()));
        }
        if (filtro.getIdCategoria() != null) {
            spec = spec.and(GastoSpecification.conCategoria(filtro.getIdCategoria()));
        }
        if (filtro.getFechaDesde() != null) {
            spec = spec.and(GastoSpecification.conFechaDesde(filtro.getFechaDesde()));
        }
        if (filtro.getFechaHasta() != null) {
            spec = spec.and(GastoSpecification.conFechaHasta(filtro.getFechaHasta()));
        }
        if (filtro.getImporteMin() != null) {
            spec = spec.and(GastoSpecification.conImporteMinimo(filtro.getImporteMin()));
        }
        if (filtro.getImporteMax() != null) {
            spec = spec.and(GastoSpecification.conImporteMaximo(filtro.getImporteMax()));
        }
        if (filtro.getTexto() != null && !filtro.getTexto().isBlank()) {
            spec = spec.and(GastoSpecification.conTexto(filtro.getTexto()));
        }

        return gastoRepository.findAll(spec).stream()
                .map(this::mapearAGastoDTO)
                .collect(Collectors.toList());
    }

    /* ============================
       CREACIÓN
       ============================ */

    @Transactional
    public GastoDTO crearGasto(CrearGastoDTO dto, Long idUsuario) {
        Usuario usuario = usuarioService.obtenerUsuarioEntidad(idUsuario);

        Gasto gasto = new Gasto();
        gasto.setDescripcion(dto.getDescripcion());
        gasto.setImporteOriginal(dto.getImporteOriginal());
        gasto.setMonedaOriginal(dto.getMonedaOriginal());
        gasto.setFechaGasto(dto.getFechaGasto());
        gasto.setEstadoGasto(EstadoGasto.BORRADOR);
        gasto.setFechaCreacion(LocalDateTime.now());
        gasto.setImagenTicket(dto.getImagenTicket());

        // Conversión de divisa usando API ExchangeRate
        BigDecimal tipoCambio = exchangeRateService.obtenerTipoCambioAEur(dto.getMonedaOriginal());
        gasto.setTipoCambio(tipoCambio);
        gasto.setImporteEur(exchangeRateService.convertirAEur(dto.getImporteOriginal(), dto.getMonedaOriginal()));

        gasto.setUsuario(usuario);
        gasto.setDepartamento(usuario.getDepartamento());

        if (dto.getIdCategoria() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));
            gasto.setCategoria(categoria);
        }

        return mapearAGastoDTO(gastoRepository.save(gasto));
    }

    /* ============================
       ELIMINACIÓN
       ============================ */

    @Transactional
    public void eliminarGasto(Long idGasto) {
        Gasto gasto = obtenerGastoValido(idGasto);
        gastoRepository.delete(gasto);
    }

    @Transactional
    public void eliminarGastos(List<Long> ids) {
        List<Gasto> gastos = gastoRepository.findAllById(ids);
        gastoRepository.deleteAll(gastos);
    }

    /* ============================
       MÁQUINA DE ESTADOS
       ============================ */

    @Transactional
    public void enviarGastoARevision(Long idGasto, Long idUsuario) {
        Gasto gasto = obtenerGastoValido(idGasto);
        Usuario usuario = usuarioService.obtenerUsuarioEntidad(idUsuario);

        if (!gasto.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new AccesoNoPermitidoException(
                    "No puedes enviar a revisión un gasto que no es tuyo"
            );
        }

        if (gasto.getEstadoGasto() != EstadoGasto.BORRADOR) {
            throw new OperacionNoPermitidaException(
                    "Solo se pueden enviar a revisión gastos en BORRADOR"
            );
        }

        cambiarEstado(
                gasto,
                EstadoGasto.PENDIENTE_APROBACION,
                usuario,
                "Gasto enviado a aprobación"
        );
    }

    @Transactional
    public void aprobarGasto(Long idGasto, Long idManager) {
        Gasto gasto = obtenerGastoValido(idGasto);
        Usuario manager = usuarioService.obtenerUsuarioEntidad(idManager);

        validarManager(manager);
        validarEstadoParaAprobacion(gasto);

        cambiarEstado(
                gasto,
                EstadoGasto.APROBADO,
                manager,
                "Gasto aprobado"
        );
    }

    @Transactional
    public void rechazarGasto(Long idGasto, Long idManager, String comentario) {
        if (comentario == null || comentario.isBlank()) {
            throw new OperacionNoPermitidaException(
                    "El comentario es obligatorio para rechazar un gasto"
            );
        }

        Gasto gasto = obtenerGastoValido(idGasto);
        Usuario manager = usuarioService.obtenerUsuarioEntidad(idManager);

        validarManager(manager);
        validarEstadoParaAprobacion(gasto);

        cambiarEstado(
                gasto,
                EstadoGasto.RECHAZADO,
                manager,
                comentario
        );
    }

    /* ============================
       AUXILIARES
       ============================ */

    private void cambiarEstado(
            Gasto gasto,
            EstadoGasto nuevoEstado,
            Usuario usuario,
            String comentario
    ) {
        EstadoGasto estadoAnterior = gasto.getEstadoGasto();
        gasto.setEstadoGasto(nuevoEstado);

        gastoRepository.save(gasto);

        auditoriaGastoService.registrarCambioEstado(
                gasto,
                estadoAnterior.name(),
                nuevoEstado.name(),
                usuario,
                comentario
        );
    }

    private GastoDTO mapearAGastoDTO(Gasto gasto) {
        GastoDTO dto = new GastoDTO();

        dto.setIdGasto(gasto.getIdGasto());
        dto.setDescripcion(gasto.getDescripcion());
        dto.setImporteOriginal(gasto.getImporteOriginal());
        dto.setMonedaOriginal(gasto.getMonedaOriginal());
        dto.setImporteEur(gasto.getImporteEur());
        dto.setEstadoGasto(gasto.getEstadoGasto());
        dto.setFechaGasto(gasto.getFechaGasto());
        dto.setImagenTicket(gasto.getImagenTicket());
        dto.setNombreDepartamento(gasto.getDepartamento().getNombre());

        if (gasto.getCategoria() != null) {
            dto.setIdCategoria(gasto.getCategoria().getIdCategoria());
            dto.setNombreCategoria(gasto.getCategoria().getNombre());
            dto.setColorCategoria(gasto.getCategoria().getColor());
        }

        return dto;
    }

    private Gasto obtenerGastoValido(Long idGasto) {
        return gastoRepository.findById(idGasto)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Gasto no encontrado")
                );
    }

    private void validarManager(Usuario usuario) {
        if (usuario.getRol() != RolUsuario.ROLE_MANAGER && usuario.getRol() != RolUsuario.ROLE_ADMIN) {
            throw new AccesoNoPermitidoException(
                    "Solo un manager o admin puede realizar esta acción"
            );
        }
    }

    private void validarEstado(Gasto gasto, EstadoGasto estadoEsperado) {
        if (gasto.getEstadoGasto() != estadoEsperado) {
            throw new OperacionNoPermitidaException(
                    "El gasto no está en el estado correcto"
            );
        }
    }

    private void validarEstadoParaAprobacion(Gasto gasto) {
        if (gasto.getEstadoGasto() == EstadoGasto.APROBADO || gasto.getEstadoGasto() == EstadoGasto.RECHAZADO) {
            throw new OperacionNoPermitidaException(
                    "No se puede modificar un gasto ya aprobado o rechazado"
            );
        }
    }
}
