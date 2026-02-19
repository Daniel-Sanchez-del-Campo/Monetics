package com.monetics.moneticsback.model.enums;

/**
 * ========================== ROL USUARIO (ENUM) ==========================
 * Enum que define los roles de seguridad del sistema.
 *
 * CONCEPTOS CLAVE PARA LA PRESENTACIÓN:
 *
 * ¿QUÉ ES UN ROL EN SPRING SECURITY?
 * - Un rol es un "permiso" que se asigna a un usuario.
 * - Spring Security los usa para decidir qué puede hacer cada usuario.
 * - Se almacenan como GrantedAuthority dentro del UserDetails y del JWT.
 *
 * ¿POR QUÉ EMPIEZAN CON "ROLE_"?
 * - Es una CONVENCIÓN obligatoria de Spring Security.
 * - Cuando usamos hasRole("USER"), Spring busca automáticamente
 *   una authority llamada "ROLE_USER" (añade el prefijo "ROLE_").
 * - Si usamos hasAuthority("ROLE_USER"), funciona directamente sin prefijo.
 * - Al incluir el prefijo en el enum, es compatible con ambos métodos.
 *
 * ¿DÓNDE SE USAN ESTOS ROLES?
 * 1) En la entidad Usuario → campo 'rol' almacenado en la BD como VARCHAR.
 * 2) En CustomUserDetailsService → se convierte a SimpleGrantedAuthority
 *    para que Spring Security lo entienda.
 * 3) En JwtUtil → se incluye en el payload del JWT como claim "rol",
 *    así cada petición lleva el rol del usuario.
 * 4) En la capa de servicio → se consulta el rol para decisiones de negocio
 *    (quién puede aprobar gastos, quién puede ver presupuestos, etc.).
 *
 * MODELO DE AUTORIZACIÓN:
 * - ROLE_USER   → nivel más bajo, acceso básico
 * - ROLE_MANAGER → nivel intermedio, gestión de equipo
 * - ROLE_ADMIN  → nivel máximo, acceso total
 * ======================================================================
 */
public enum RolUsuario {

    // Usuario estándar: puede crear y ver solo sus propios gastos
    ROLE_USER,

    // Manager: puede ver y aprobar los gastos de su equipo
    ROLE_MANAGER,

    // Administrador: puede gestionar presupuestos y ver información global
    ROLE_ADMIN
}
