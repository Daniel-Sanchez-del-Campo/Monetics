export interface Usuario {
  idUsuario: number;
  nombre: string;
  email: string;
  rol: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  usuario: Usuario;
}

export interface RegisterRequest {
  nombre: string;
  email: string;
  password: string;
  idDepartamento: number;
}

export interface AdminUsuario {
  idUsuario: number;
  nombre: string;
  email: string;
  rol: string;
  activo: boolean;
  departamentoNombre: string;
  idDepartamento: number;
}

export interface UpdateUsuarioAdmin {
  rol?: string;
  activo?: boolean;
}
