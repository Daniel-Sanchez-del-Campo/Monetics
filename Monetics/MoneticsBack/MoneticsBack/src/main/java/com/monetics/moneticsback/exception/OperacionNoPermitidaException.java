package com.monetics.moneticsback.exception;

/**
 * Excepción que indica que una operación no es válida
 * según las reglas de negocio del sistema.
 *
 * Se utiliza para:
 * - Errores de estado
 * - Validaciones de reglas de negocio
 *
 * Esta excepción se convierte en HTTP 400 (Bad Request).
 */
public class OperacionNoPermitidaException extends RuntimeException {

    public OperacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}
