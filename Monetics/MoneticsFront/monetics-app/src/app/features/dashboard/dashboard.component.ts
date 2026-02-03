import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AuthService, GastoService } from '../../core/services';
import { Gasto, Usuario } from '../../core/models';
import { NuevoGastoDialogComponent } from './nuevo-gasto-dialog/nuevo-gasto-dialog.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatTableModule,
    MatChipsModule,
    MatMenuModule,
    MatProgressSpinnerModule,
    MatDialogModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  currentUser: Usuario | null = null;
  gastos: Gasto[] = [];
  loading = true;
  displayedColumns: string[] = ['descripcion', 'fechaGasto', 'importeOriginal', 'importeEur', 'estadoGasto'];

  constructor(
    public authService: AuthService,
    private gastoService: GastoService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user) {
        this.cargarGastos();
      }
    });
  }

  cargarGastos(): void {
    if (!this.currentUser) return;

    this.loading = true;

    if (this.authService.isManager) {
      this.gastoService.obtenerGastosDelEquipo(this.currentUser.idUsuario).subscribe({
        next: (gastos) => {
          this.gastos = gastos;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
    } else {
      this.gastoService.obtenerGastosPorUsuario(this.currentUser.idUsuario).subscribe({
        next: (gastos) => {
          this.gastos = gastos;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
    }
  }

  nuevoGasto(): void {
    const dialogRef = this.dialog.open(NuevoGastoDialogComponent, {
      width: '500px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.cargarGastos();
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getEstadoColor(estado: string): string {
    switch(estado) {
      case 'APROBADO': return 'primary';
      case 'RECHAZADO': return 'warn';
      default: return 'accent';
    }
  }
}
