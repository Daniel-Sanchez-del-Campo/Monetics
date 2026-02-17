import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuditoriaService } from '../../../core/services';
import { AuditoriaGasto } from '../../../core/models';

@Component({
  selector: 'app-historial-gasto-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './historial-gasto-dialog.component.html',
  styleUrl: './historial-gasto-dialog.component.css'
})
export class HistorialGastoDialogComponent implements OnInit {
  historial: AuditoriaGasto[] = [];
  loading = true;

  constructor(
    private auditoriaService: AuditoriaService,
    private dialogRef: MatDialogRef<HistorialGastoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { idGasto: number; descripcion: string }
  ) {}

  ngOnInit(): void {
    this.auditoriaService.obtenerHistorial(this.data.idGasto).subscribe({
      next: (historial) => {
        this.historial = historial;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  getEstadoIcon(estado: string): string {
    switch (estado) {
      case 'BORRADOR': return 'edit_note';
      case 'PENDIENTE_APROBACION': return 'hourglass_top';
      case 'APROBADO': return 'check_circle';
      case 'RECHAZADO': return 'cancel';
      default: return 'circle';
    }
  }

  getEstadoClass(estado: string): string {
    switch (estado) {
      case 'APROBADO': return 'estado-aprobado';
      case 'RECHAZADO': return 'estado-rechazado';
      case 'PENDIENTE_APROBACION': return 'estado-pendiente';
      default: return 'estado-borrador';
    }
  }

  cerrar(): void {
    this.dialogRef.close();
  }
}
