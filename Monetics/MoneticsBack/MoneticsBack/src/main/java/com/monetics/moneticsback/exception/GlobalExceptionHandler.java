package com.monetics.moneticsback.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones del backend.
 *
 * Centraliza la conversión de excepciones de negocio
 * en respuestas HTTP coherentes.
 *
 * Evita que los controllers tengan bloques try/catch
 * y mantiene una arquitectura limpia.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja recursos no encontrados.
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoNoEncontrado(
            RecursoNoEncontradoException ex
    ) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Maneja accesos no permitidos.
     */
    @ExceptionHandler(AccesoNoPermitidoException.class)
    public ResponseEntity<Map<String, Object>> manejarAccesoNoPermitido(
            AccesoNoPermitidoException ex
    ) {
        return construirRespuesta(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    /**
     * Maneja operaciones no válidas según el negocio.
     */
    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ResponseEntity<Map<String, Object>> manejarOperacionNoPermitida(
            OperacionNoPermitidaException ex
    ) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Método auxiliar para construir una respuesta estándar de error.
     *
     * Todas las respuestas de error tienen:
     * - timestamp
     * - status HTTP
     * - mensaje claro
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
