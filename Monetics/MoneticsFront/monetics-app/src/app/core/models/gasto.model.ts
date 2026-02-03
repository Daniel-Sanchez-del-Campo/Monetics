export interface Gasto {
  idGasto: number;
  descripcion: string;
  importeOriginal: number;
  monedaOriginal: string;
  importeEur: number;
  estadoGasto: EstadoGasto;
  fechaGasto: string;
  nombreDepartamento: string;
}

export enum EstadoGasto {
  PENDIENTE = 'PENDIENTE',
  APROBADO = 'APROBADO',
  RECHAZADO = 'RECHAZADO'
}

export interface CrearGasto {
  descripcion: string;
  importeOriginal: number;
  monedaOriginal: string;
  fechaGasto: string;
}
