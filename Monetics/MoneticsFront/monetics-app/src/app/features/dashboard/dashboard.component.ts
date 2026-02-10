import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SelectionModel } from '@angular/cdk/collections';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatCheckboxModule } from '@angular/material/checkbox';
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
    MatCheckboxModule,
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
  selection = new SelectionModel<Gasto>(true, []);
  imagenAbierta: string | null = null;

  get displayedColumns(): string[] {
    const cols = ['descripcion', 'fechaGasto', 'importeOriginal', 'importeEur', 'estadoGasto', 'acciones'];
    if (this.authService.isAdmin) {
      return ['select', ...cols];
    }
    return cols;
  }

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
    this.selection.clear();

    let obs;
    if (this.authService.isAdmin) {
      obs = this.gastoService.obtenerTodosGastos();
    } else if (this.authService.isManager) {
      obs = this.gastoService.obtenerGastosDelEquipo(this.currentUser.idUsuario);
    } else {
      obs = this.gastoService.obtenerGastosPorUsuario(this.currentUser.idUsuario);
    }

    obs.subscribe({
      next: (gastos) => {
        this.gastos = gastos;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  isAllSelected(): boolean {
    return this.selection.selected.length === this.gastos.length;
  }

  masterToggle(): void {
    if (this.isAllSelected()) {
      this.selection.clear();
    } else {
      this.gastos.forEach(row => this.selection.select(row));
    }
  }

  eliminarSeleccionados(): void {
    const ids = this.selection.selected.map(g => g.idGasto);
    if (ids.length === 0) return;

    this.gastoService.eliminarGastos(ids).subscribe({
      next: () => {
        this.cargarGastos();
      },
      error: () => {
        // Error silencioso - podria mostrar un snackbar
      }
    });
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

  enviarARevision(gasto: Gasto): void {
    if (!this.currentUser) return;
    this.gastoService.enviarARevision(gasto.idGasto, this.currentUser.idUsuario).subscribe({
      next: () => this.cargarGastos(),
      error: () => {}
    });
  }

  aprobarGasto(gasto: Gasto): void {
    if (!this.currentUser) return;
    this.gastoService.aprobarGasto(gasto.idGasto, this.currentUser.idUsuario).subscribe({
      next: () => this.cargarGastos(),
      error: () => {}
    });
  }

  rechazarGasto(gasto: Gasto): void {
    if (!this.currentUser) return;
    const comentario = prompt('Motivo del rechazo:');
    if (!comentario) return;
    this.gastoService.rechazarGasto(gasto.idGasto, this.currentUser.idUsuario, comentario).subscribe({
      next: () => this.cargarGastos(),
      error: () => {}
    });
  }

  verImagen(gasto: Gasto): void {
    if (gasto.imagenTicket) {
      this.imagenAbierta = gasto.imagenTicket;
    }
  }

  cerrarImagen(): void {
    this.imagenAbierta = null;
  }
}
