package com.monetics.moneticsback.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ========================== GLOBAL EXCEPTION HANDLER ==========================
 * Manejador global de excepciones del backend.
 *
 * CONCEPTOS CLAVE PARA LA PRESENTACIÓN:
 *
 * ¿QUÉ ES @RestControllerAdvice?
 * - Es un componente de Spring que intercepta TODAS las excepciones lanzadas
 *   desde cualquier Controller de la aplicación.
 * - Centraliza el manejo de errores en un solo lugar, evitando bloques
 *   try/catch repetidos en cada Controller.
 *
 * RELACIÓN CON SPRING SECURITY:
 * - Spring Security gestiona automáticamente los errores de autenticación
 *   (401 Unauthorized cuando las credenciales son incorrectas o el JWT es inválido).
 * - Este handler gestiona los errores de AUTORIZACIÓN a nivel de negocio:
 *
 *   · AccesoNoPermitidoException → HTTP 403 FORBIDDEN
 *     Se lanza cuando un usuario autenticado intenta acceder a un recurso
 *     que NO le pertenece o para el que NO tiene el rol adecuado.
 *     Ejemplo: un ROLE_USER intenta aprobar un gasto (solo ROLE_MANAGER puede).
 *
 *   · RecursoNoEncontradoException → HTTP 404 NOT FOUND
 *     Se lanza cuando se busca un usuario, gasto o recurso que no existe.
 *     También puede ocurrir durante la autenticación si el email no existe.
 *
 *   · OperacionNoPermitidaException → HTTP 400 BAD REQUEST
 *     Se lanza cuando una operación viola reglas de negocio.
 *     Ejemplo: intentar aprobar un gasto que ya fue aprobado.
 *
 * FLUJO DE ERRORES DE SEGURIDAD:
 *   1) JWT inválido/expirado → Spring Security devuelve 401 (automático)
 *   2) Sin JWT en endpoint protegido → Spring Security devuelve 401 (automático)
 *   3) Usuario sin permiso para un recurso → AccesoNoPermitidoException → 403 (este handler)
 *   4) Recurso no encontrado → RecursoNoEncontradoException → 404 (este handler)
 * =============================================================================
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja recursos no encontrados → HTTP 404.
     * Se lanza cuando un usuario, gasto, departamento, etc. no existe en la BD.
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoNoEncontrado(
            RecursoNoEncontradoException ex
    ) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Maneja accesos no permitidos → HTTP 403 FORBIDDEN.
     *
     * IMPORTANTE PARA SEGURIDAD:
     * - Se lanza desde la capa de servicio cuando un usuario autenticado
     *   intenta realizar una acción para la que no tiene permiso.
     * - Diferencia con 401: el 401 significa "no estás autenticado" (no hay JWT),
     *   mientras que el 403 significa "estás autenticado pero no tienes permiso".
     */
    @ExceptionHandler(AccesoNoPermitidoException.class)
    public ResponseEntity<Map<String, Object>> manejarAccesoNoPermitido(
            AccesoNoPermitidoException ex
    ) {
        return construirRespuesta(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    /**
     * Maneja operaciones no válidas según reglas de negocio → HTTP 400.
     * Ejemplo: intentar cambiar el estado de un gasto de forma inválida.
     */
    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ResponseEntity<Map<String, Object>> manejarOperacionNoPermitida(
            OperacionNoPermitidaException ex
    ) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Construye una respuesta de error estandarizada en formato JSON.
     *
     * Formato de respuesta:
     * {
     *   "timestamp": "2024-02-16T10:30:45.123",
     *   "status": 403,
     *   "error": "Forbidden",
     *   "mensaje": "No tienes permiso para acceder a este recurso"
     * }
     */
    private ResponseEntity<Map<String, Object>> construirRespuesta(
            HttpStatus status,
            String mensaje
    ) {
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", status.value());
        cuerpo.put("error", status.getReasonPhrase());
        cuerpo.put("mensaje", mensaje);

        return new ResponseEntity<>(cuerpo, status);
    }
}
