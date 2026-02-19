import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DriveUploadResponse, AnalisisTicketResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private apiUrl = '/api/tickets';

  constructor(private http: HttpClient) {}

  /**
   * Sube una imagen de ticket a Google Drive via el backend.
   */
  subirImagen(archivo: File, idUsuario: number): Observable<DriveUploadResponse> {
    const formData = new FormData();
    formData.append('archivo', archivo);
    formData.append('idUsuario', idUsuario.toString());

    return this.http.post<DriveUploadResponse>(
      `${this.apiUrl}/subir-imagen`,
      formData
    );
  }

  /**
   * Analiza una imagen de ticket con IA y devuelve los datos extraidos.
   */
  analizarTicket(archivo: File): Observable<AnalisisTicketResponse> {
    const formData = new FormData();
    formData.append('archivo', archivo);

    return this.http.post<AnalisisTicketResponse>(
      `${this.apiUrl}/analizar`,
      formData
    );
  }

  /**
   * Obtiene una imagen de ticket desde el almacenamiento local del backend.
   */
  obtenerImagen(nombreArchivo: string): Observable<Blob> {
    return this.http.get(
      `${this.apiUrl}/imagen/${nombreArchivo}`,
      { responseType: 'blob' }
    );
  }

  /**
   * Elimina una imagen de Google Drive.
   */
  eliminarImagen(fileId: string): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/imagen/${fileId}`
    );
  }
}
