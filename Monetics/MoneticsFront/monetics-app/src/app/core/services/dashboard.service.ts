import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { DashboardData, Departamento } from '../models';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  constructor(private apiService: ApiService) {}

  obtenerDashboard(): Observable<DashboardData> {
    return this.apiService.get<DashboardData>('/dashboard');
  }

  obtenerDepartamentos(): Observable<Departamento[]> {
    return this.apiService.get<Departamento[]>('/departamentos');
  }

  actualizarPresupuesto(idDepartamento: number, presupuestoMensual: number, presupuestoAnual: number): Observable<Departamento> {
    return this.apiService.put<Departamento>(`/departamentos/${idDepartamento}/presupuesto`, {
      presupuestoMensual,
      presupuestoAnual
    });
  }
}
