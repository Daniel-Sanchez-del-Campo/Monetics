export interface AuditoriaGasto {
  idAuditoria: number;
  estadoAnterior: string;
  estadoNuevo: string;
  fechaCambio: string;
  comentario: string;
  nombreUsuarioAccion: string;
}
