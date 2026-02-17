export interface Categoria {
  idCategoria: number;
  nombre: string;
  descripcion: string;
  color: string;
  activa: boolean;
}

export interface CrearCategoria {
  nombre: string;
  descripcion?: string;
  color?: string;
}
