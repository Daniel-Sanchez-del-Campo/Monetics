import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
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
}
