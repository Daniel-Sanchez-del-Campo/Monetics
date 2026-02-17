import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Categoria, CrearCategoria } from '../models';

@Injectable({
  providedIn: 'root'
})
export class CategoriaService {

  constructor(private apiService: ApiService) {}

  obtenerActivas(): Observable<Categoria[]> {
    return this.apiService.get<Categoria[]>('/categorias');
  }

  obtenerTodas(): Observable<Categoria[]> {
    return this.apiService.get<Categoria[]>('/categorias/todas');
  }

  crear(categoria: CrearCategoria): Observable<Categoria> {
    return this.apiService.post<Categoria>('/categorias', categoria);
  }

  actualizar(id: number, categoria: CrearCategoria): Observable<Categoria> {
    return this.apiService.put<Categoria>(`/categorias/${id}`, categoria);
  }

  desactivar(id: number): Observable<void> {
    return this.apiService.delete<void>(`/categorias/${id}`);
  }
}
