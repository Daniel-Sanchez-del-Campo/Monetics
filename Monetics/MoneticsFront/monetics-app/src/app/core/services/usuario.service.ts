import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Usuario, AdminUsuario, UpdateUsuarioAdmin } from '../models';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  constructor(private apiService: ApiService) {}

  obtenerUsuarioPorId(idUsuario: number): Observable<Usuario> {
    return this.apiService.get<Usuario>(`/usuarios/${idUsuario}`);
  }

  obtenerEmpleadosDeManager(idManager: number): Observable<Usuario[]> {
    return this.apiService.get<Usuario[]>(`/usuarios/manager/${idManager}/empleados`);
  }

  obtenerTodosLosUsuariosAdmin(): Observable<AdminUsuario[]> {
    return this.apiService.get<AdminUsuario[]>('/admin/usuarios');
  }

  actualizarUsuarioAdmin(idUsuario: number, data: UpdateUsuarioAdmin): Observable<AdminUsuario> {
    return this.apiService.put<AdminUsuario>(`/admin/usuarios/${idUsuario}`, data);
  }
}
