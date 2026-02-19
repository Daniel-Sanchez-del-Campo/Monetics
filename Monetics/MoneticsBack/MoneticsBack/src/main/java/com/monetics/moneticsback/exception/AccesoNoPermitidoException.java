package com.monetics.moneticsback.exception;

/**
 * Excepción que indica que un usuario no tiene permisos
 * para realizar una determinada acción.
 *
 * Se utiliza para:
 * - Control de roles
 * - Control de propiedad de recursos
 *
 * Esta excepción se convierte en HTTP 403 (Forbidden).
 */
public class AccesoNoPermitidoException extends RuntimeException {

    public AccesoNoPermitidoException(String mensaje) {
        super(mensaje);
    }
}
