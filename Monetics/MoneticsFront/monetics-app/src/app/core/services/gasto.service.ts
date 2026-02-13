import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { ApiService } from './api.service';
import { Gasto, CrearGasto } from '../models';

@Injectable({
  providedIn: 'root'
})
export class GastoService {

  constructor(private apiService: ApiService) {}

  obtenerGastosPorUsuario(idUsuario: number): Observable<Gasto[]> {
    return this.apiService.get<Gasto[]>(`/gastos/usuario/${idUsuario}`);
  }

  obtenerGastosDelEquipo(idManager: number): Observable<Gasto[]> {
    return this.apiService.get<Gasto[]>(`/gastos/manager/${idManager}`);
  }

  crearGasto(idUsuario: number, gasto: CrearGasto): Observable<Gasto> {
    return this.apiService.post<Gasto>(`/gastos/usuario/${idUsuario}`, gasto);
  }

  eliminarGastos(ids: number[]): Observable<void> {
    return this.apiService.post<void>('/gastos/eliminar-batch', ids);
  }

  obtenerTodosGastos(): Observable<Gasto[]> {
    return this.apiService.get<Gasto[]>('/gastos');
  }

  filtrarGastos(filtros: { [key: string]: any }): Observable<Gasto[]> {
    let params = new HttpParams();
    Object.keys(filtros).forEach(key => {
      if (filtros[key] !== null && filtros[key] !== undefined && filtros[key] !== '') {
        params = params.set(key, filtros[key].toString());
      }
    });
    return this.apiService.get<Gasto[]>('/gastos/filtrar', params);
  }

  enviarARevision(idGasto: number, idUsuario: number): Observable<void> {
    return this.apiService.put<void>(`/gastos/${idGasto}/enviar-revision?idUsuario=${idUsuario}`, {});
  }

  aprobarGasto(idGasto: number, idManager: number): Observable<void> {
    return this.apiService.put<void>(`/gastos/${idGasto}/aprobar?idManager=${idManager}`, {});
  }

  rechazarGasto(idGasto: number, idManager: number, comentario: string): Observable<void> {
    return this.apiService.put<void>(`/gastos/${idGasto}/rechazar?idManager=${idManager}`, { comentario });
  }
}
