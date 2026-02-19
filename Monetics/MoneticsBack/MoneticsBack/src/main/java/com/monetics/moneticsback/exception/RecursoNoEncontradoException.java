package com.monetics.moneticsback.exception;

/**
 * Excepción que indica que un recurso solicitado no existe.
 *
 * Se utiliza cuando:
 * - No se encuentra un usuario
 * - No se encuentra un gasto
 * - No se encuentra cualquier entidad requerida
 *
 * Esta excepción será capturada por el manejador global
 * y convertida en una respuesta HTTP 404 (Not Found).
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
