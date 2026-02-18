export interface Gasto {
  idGasto: number;
  descripcion: string;
  importeOriginal: number;
  monedaOriginal: string;
  importeEur: number;
  estadoGasto: EstadoGasto;
  fechaGasto: string;
  nombreDepartamento: string;
  idCategoria?: number;
  nombreCategoria?: string;
  colorCategoria?: string;

  // Campos de Drive (nuevos - sustituyen a imagenTicket)
  driveFileId?: string;
  driveFileUrl?: string;
  imagenNombre?: string;

  // Campos de IA (nuevos)
  analizadoPorIa?: boolean;
  iaConfianza?: number;

  // DEPRECADO - se eliminara tras migracion
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
  idCategoria?: number;
  driveFileId?: string;
  driveFileUrl?: string;
  imagenNombre?: string;
  analizadoPorIa?: boolean;
  iaConfianza?: number;
}

// Respuesta de subida a Drive
export interface DriveUploadResponse {
  driveFileId: string;
  driveFileUrl: string;
  imagenNombre: string;
}

// Respuesta del analisis de IA
export interface AnalisisTicketResponse {
  descripcion: string | null;
  importeOriginal: number | null;
  monedaOriginal: string | null;
  fechaGasto: string | null;
  categoriaSugerida: string | null;
  idCategoriaSugerida: number | null;
  confianza: number;
  confianzaPorCampo: {
    descripcion: number;
    importe: number;
    moneda: number;
    fecha: number;
    categoria: number;
  };
}
