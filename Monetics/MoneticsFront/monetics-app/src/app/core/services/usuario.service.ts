import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Usuario } from '../models';

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
}
