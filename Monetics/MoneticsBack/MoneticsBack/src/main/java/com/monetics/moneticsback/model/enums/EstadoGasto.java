package com.monetics.moneticsback.model.enums;

/**
 * Enum que define los posibles estados de un gasto corporativo.
 *
 * Implementa la máquina de estados del negocio:
 * BORRADOR -> PENDIENTE_APROBACION -> APROBADO / RECHAZADO
 *
 * Se utiliza en:
 * - La entidad Gasto
 * - Lógica de negocio (validación de transiciones)
 * - Auditoría de cambios de estado
 */
public enum EstadoGasto {

    // El gasto está en edición y aún no se ha enviado a aprobación
    BORRADOR,

    // El gasto ha sido enviado y está pendiente de revisión por un manager
    PENDIENTE_APROBACION,

    // El gasto ha sido aprobado y queda pendiente de reembolso
    APROBADO,

    // El gasto ha sido revisado y rechazado por un manager
    RECHAZADO
}
