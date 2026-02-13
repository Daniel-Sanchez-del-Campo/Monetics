export interface DashboardData {
  gastosPorDepartamento: GastoPorDepartamento[];
  alertasPresupuesto: AlertaPresupuesto[];
  totalPendienteReembolso: number;
  totalAprobado: number;
  totalGastosMes: number;
  totalGastosMesAnterior: number;
  variacionMensualPorcentaje: number | null;
  gastosPorCategoria: GastoPorCategoria[];
  totalGastos: number;
  gastosPendientes: number;
  gastosAprobados: number;
  gastosRechazados: number;
}

export interface GastoPorDepartamento {
  departamento: string;
  totalGastado: number;
  presupuestoMensual: number;
  numGastos: number;
}

export interface AlertaPresupuesto {
  departamento: string;
  presupuestoMensual: number;
  gastoActual: number;
  porcentajeUsado: number;
  nivelAlerta: string;
}

export interface GastoPorCategoria {
  categoria: string;
  color: string;
  totalGastado: number;
  numGastos: number;
}

export interface Departamento {
  idDepartamento: number;
  nombre: string;
  presupuestoMensual: number;
  presupuestoAnual: number;
}
