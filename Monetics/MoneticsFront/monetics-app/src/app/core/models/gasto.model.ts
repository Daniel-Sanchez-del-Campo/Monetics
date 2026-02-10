export interface Gasto {
  idGasto: number;
  descripcion: string;
  importeOriginal: number;
  monedaOriginal: string;
  importeEur: number;
  estadoGasto: EstadoGasto;
  fechaGasto: string;
  nombreDepartamento: string;
  imagenTicket?: string;
}

export enum EstadoGasto {
  BORRADOR = 'BORRADOR',
  PENDIENTE_APROBACION = 'PENDIENTE_APROBACION',
  APROBADO = 'APROBADO',
  RECHAZADO = 'RECHAZADO'
}

export interface CrearGasto {
  descripcion: string;
  importeOriginal: number;
  monedaOriginal: string;
  fechaGasto: string;
  imagenTicket?: string;
}
