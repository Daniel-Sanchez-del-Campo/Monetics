package com.monetics.moneticsback.model.enums;

/**
 * Enum que define el tipo de periodo al que pertenece un presupuesto.
 *
 * Se utiliza en:
 * - La entidad Presupuesto
 * - Cálculos de control de gasto
 * - Alertas de presupuestos excedidos
 *
 * Permite diferenciar entre presupuestos mensuales y anuales.
 */
public enum TipoPeriodo {

    // Presupuesto aplicable a un mes concreto
    MENSUAL,

    // Presupuesto aplicable a todo el año
    ANUAL
}
