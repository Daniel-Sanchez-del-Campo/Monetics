import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { AuditoriaGasto } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AuditoriaService {

  constructor(private apiService: ApiService) {}

  obtenerHistorial(idGasto: number): Observable<AuditoriaGasto[]> {
    return this.apiService.get<AuditoriaGasto[]>(`/auditorias/gasto/${idGasto}`);
  }
}
