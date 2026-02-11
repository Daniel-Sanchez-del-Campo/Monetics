import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { AuthService, GastoService, DashboardService } from '../../core/services';
import { Gasto, Usuario, DashboardData, Departamento } from '../../core/models';
import { NuevoGastoDialogComponent } from './nuevo-gasto-dialog/nuevo-gasto-dialog.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatTableModule,
    MatCheckboxModule,
    MatChipsModule,
    MatMenuModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatInputModule,
    MatFormFieldModule,
    BaseChartDirective
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

  // Dashboard data
  dashboardData: DashboardData | null = null;
  departamentos: Departamento[] = [];
  editingDepto: Departamento | null = null;
  editPresMensual = 0;
  editPresAnual = 0;

  // Charts
  barChartData: ChartConfiguration<'bar'>['data'] = { labels: [], datasets: [] };
  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { labels: { color: '#e0e6ed' } }
    },
    scales: {
      x: { ticks: { color: '#a0aec0' }, grid: { color: 'rgba(255,255,255,0.06)' } },
      y: { ticks: { color: '#a0aec0' }, grid: { color: 'rgba(255,255,255,0.06)' } }
    }
  };

  doughnutChartData: ChartConfiguration<'doughnut'>['data'] = { labels: [], datasets: [] };
  doughnutChartOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'bottom', labels: { color: '#e0e6ed', padding: 16 } }
    }
  };

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
    private dashboardService: DashboardService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user) {
        this.cargarGastos();
        this.cargarDashboard();
        if (this.authService.isAdmin) {
          this.cargarDepartamentos();
        }
      }
    });
  }

  cargarDashboard(): void {
    this.dashboardService.obtenerDashboard().subscribe({
      next: (data) => {
        this.dashboardData = data;
        this.actualizarGraficos(data);
      },
      error: () => {}
    });
  }

  cargarDepartamentos(): void {
    this.dashboardService.obtenerDepartamentos().subscribe({
      next: (deptos) => this.departamentos = deptos,
      error: () => {}
    });
  }

  actualizarGraficos(data: DashboardData): void {
    // Bar chart: gastos por departamento vs presupuesto
    this.barChartData = {
      labels: data.gastosPorDepartamento.map(d => d.departamento),
      datasets: [
        {
          data: data.gastosPorDepartamento.map(d => d.totalGastado),
          label: 'Gastado',
          backgroundColor: 'rgba(100, 181, 246, 0.7)',
          borderColor: '#64b5f6',
          borderWidth: 1
        },
        {
          data: data.gastosPorDepartamento.map(d => d.presupuestoMensual),
          label: 'Presupuesto mensual',
          backgroundColor: 'rgba(255, 255, 255, 0.15)',
          borderColor: 'rgba(255, 255, 255, 0.4)',
          borderWidth: 1
        }
      ]
    };

    // Doughnut: estados
    this.doughnutChartData = {
      labels: ['Pendientes', 'Aprobados', 'Rechazados'],
      datasets: [{
        data: [data.gastosPendientes, data.gastosAprobados, data.gastosRechazados],
        backgroundColor: ['rgba(255, 183, 77, 0.8)', 'rgba(129, 199, 132, 0.8)', 'rgba(239, 154, 154, 0.8)'],
        borderColor: ['#ffb74d', '#81c784', '#ef9a9a'],
        borderWidth: 2
      }]
    };
  }

  editarPresupuesto(depto: Departamento): void {
    this.editingDepto = depto;
    this.editPresMensual = depto.presupuestoMensual;
    this.editPresAnual = depto.presupuestoAnual;
  }

  cancelarEdicion(): void {
    this.editingDepto = null;
  }

  guardarPresupuesto(): void {
    if (!this.editingDepto) return;
    this.dashboardService.actualizarPresupuesto(
      this.editingDepto.idDepartamento,
      this.editPresMensual,
      this.editPresAnual
    ).subscribe({
      next: () => {
        this.editingDepto = null;
        this.cargarDashboard();
        this.cargarDepartamentos();
      },
      error: () => {}
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
        this.cargarDashboard();
      },
      error: () => {}
    });
  }

  nuevoGasto(): void {
    const dialogRef = this.dialog.open(NuevoGastoDialogComponent, {
      width: '500px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.cargarGastos();
        this.cargarDashboard();
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
      next: () => { this.cargarGastos(); this.cargarDashboard(); },
      error: () => {}
    });
  }

  aprobarGasto(gasto: Gasto): void {
    if (!this.currentUser) return;
    this.gastoService.aprobarGasto(gasto.idGasto, this.currentUser.idUsuario).subscribe({
      next: () => { this.cargarGastos(); this.cargarDashboard(); },
      error: () => {}
    });
  }

  rechazarGasto(gasto: Gasto): void {
    if (!this.currentUser) return;
    const comentario = prompt('Motivo del rechazo:');
    if (!comentario) return;
    this.gastoService.rechazarGasto(gasto.idGasto, this.currentUser.idUsuario, comentario).subscribe({
      next: () => { this.cargarGastos(); this.cargarDashboard(); },
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
