package com.monetics.moneticsback.model.enums;

/**
 * Enum que representa los roles de seguridad del sistema.
 *
 * Se utiliza en:
 * - La entidad Usuario (campo rol)
 * - Spring Security para autorización
 * - Generación y validación de JWT
 *
 * Los valores deben coincidir exactamente con los roles definidos
 * en la configuración de seguridad.
 */
public enum RolUsuario {

    // Usuario estándar: puede crear y ver solo sus propios gastos
    ROLE_USER,

    // Manager: puede ver y aprobar los gastos de su equipo
    ROLE_MANAGER,

    // Administrador: puede gestionar presupuestos y ver información global
    ROLE_ADMIN
}
