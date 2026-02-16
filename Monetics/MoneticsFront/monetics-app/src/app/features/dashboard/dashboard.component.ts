import { Component, OnInit } from '@angular/core';
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
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { AuthService, GastoService, DashboardService, CategoriaService } from '../../core/services';
import { Gasto, Usuario, DashboardData, Departamento, Categoria } from '../../core/models';
import { NuevoGastoDialogComponent } from './nuevo-gasto-dialog/nuevo-gasto-dialog.component';
import { HistorialGastoDialogComponent } from './historial-gasto-dialog/historial-gasto-dialog.component';

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
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatExpansionModule,
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
  categorias: Categoria[] = [];
  editingDepto: Departamento | null = null;
  editPresMensual = 0;
  editPresAnual = 0;

  // Filtros
  filtrosAbiertos = false;
  filtroEstado: string = '';
  filtroDepartamento: number | null = null;
  filtroCategoria: number | null = null;
  filtroFechaDesde: Date | null = null;
  filtroFechaHasta: Date | null = null;
  filtroImporteMin: number | null = null;
  filtroImporteMax: number | null = null;
  filtroTexto: string = '';
  filtrosActivos = false;

  // Charts
  barChartData: ChartConfiguration<'bar'>['data'] = { labels: [], datasets: [] };
  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { labels: { color: '#334155', font: { family: 'Inter', size: 12 }, padding: 16 } }
    },
    scales: {
      x: {
        ticks: { color: '#64748B', font: { family: 'Inter', size: 11 } },
        grid: { color: 'rgba(0,0,0,0.06)' },
        border: { color: 'rgba(0,0,0,0.1)' }
      },
      y: {
        ticks: { color: '#64748B', font: { family: 'Inter', size: 11 } },
        grid: { color: 'rgba(0,0,0,0.06)' },
        border: { color: 'rgba(0,0,0,0.1)' }
      }
    }
  };

  doughnutChartData: ChartConfiguration<'doughnut'>['data'] = { labels: [], datasets: [] };
  doughnutChartOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    cutout: '55%',
    plugins: {
      legend: { position: 'bottom', labels: { color: '#334155', font: { family: 'Inter', size: 12 }, padding: 20, usePointStyle: true, pointStyle: 'circle' } }
    }
  };

  categoryChartData: ChartConfiguration<'doughnut'>['data'] = { labels: [], datasets: [] };
  categoryChartOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    cutout: '55%',
    plugins: {
      legend: { position: 'bottom', labels: { color: '#334155', font: { family: 'Inter', size: 12 }, padding: 20, usePointStyle: true, pointStyle: 'circle' } }
    }
  };

  get displayedColumns(): string[] {
    const cols = ['descripcion', 'categoria', 'fechaGasto', 'importeOriginal', 'importeEur', 'estadoGasto', 'acciones'];
    if (this.authService.isAdmin) {
      return ['select', ...cols];
    }
    return cols;
  }

  constructor(
    public authService: AuthService,
    private gastoService: GastoService,
    private dashboardService: DashboardService,
    private categoriaService: CategoriaService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user) {
        this.cargarGastos();
        this.cargarDashboard();
        this.cargarCategorias();
        if (this.authService.isAdmin) {
          this.cargarDepartamentos();
        }
      }
    });
  }

  cargarCategorias(): void {
    this.categoriaService.obtenerActivas().subscribe({
      next: (cats) => this.categorias = cats,
      error: () => {}
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
          backgroundColor: 'rgba(30, 58, 95, 0.75)',
          borderColor: '#1E3A5F',
          borderWidth: 1,
          borderRadius: 4
        },
        {
          data: data.gastosPorDepartamento.map(d => d.presupuestoMensual),
          label: 'Presupuesto mensual',
          backgroundColor: 'rgba(148, 163, 184, 0.3)',
          borderColor: '#94A3B8',
          borderWidth: 1,
          borderRadius: 4
        }
      ]
    };

    // Doughnut: estados
    this.doughnutChartData = {
      labels: ['Pendientes', 'Aprobados', 'Rechazados'],
      datasets: [{
        data: [data.gastosPendientes, data.gastosAprobados, data.gastosRechazados],
        backgroundColor: ['#D97706', '#16A34A', '#DC2626'],
        borderColor: 'transparent',
        borderWidth: 0,
        spacing: 2
      }]
    };

    // Doughnut: categorias
    if (data.gastosPorCategoria && data.gastosPorCategoria.length > 0) {
      this.categoryChartData = {
        labels: data.gastosPorCategoria.map(c => c.categoria),
        datasets: [{
          data: data.gastosPorCategoria.map(c => c.totalGastado),
          backgroundColor: data.gastosPorCategoria.map(c => c.color),
          borderColor: 'transparent',
          borderWidth: 0,
          spacing: 2
        }]
      };
    }
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

  // === Filtros ===
  aplicarFiltros(): void {
    if (!this.currentUser) return;
    this.loading = true;
    this.selection.clear();

    const rol = this.authService.isAdmin ? 'ROLE_ADMIN'
      : this.authService.isManager ? 'ROLE_MANAGER' : 'ROLE_USER';

    const filtros: any = {
      idUsuario: this.currentUser.idUsuario,
      rol: rol
    };

    if (this.filtroEstado) filtros.estadoGasto = this.filtroEstado;
    if (this.filtroDepartamento) filtros.idDepartamento = this.filtroDepartamento;
    if (this.filtroCategoria) filtros.idCategoria = this.filtroCategoria;
    if (this.filtroFechaDesde) filtros.fechaDesde = this.formatDate(this.filtroFechaDesde);
    if (this.filtroFechaHasta) filtros.fechaHasta = this.formatDate(this.filtroFechaHasta);
    if (this.filtroImporteMin !== null) filtros.importeMin = this.filtroImporteMin;
    if (this.filtroImporteMax !== null) filtros.importeMax = this.filtroImporteMax;
    if (this.filtroTexto) filtros.texto = this.filtroTexto;

    this.filtrosActivos = true;

    this.gastoService.filtrarGastos(filtros).subscribe({
      next: (gastos) => {
        this.gastos = gastos;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  limpiarFiltros(): void {
    this.filtroEstado = '';
    this.filtroDepartamento = null;
    this.filtroCategoria = null;
    this.filtroFechaDesde = null;
    this.filtroFechaHasta = null;
    this.filtroImporteMin = null;
    this.filtroImporteMax = null;
    this.filtroTexto = '';
    this.filtrosActivos = false;
    this.cargarGastos();
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
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

  verHistorial(gasto: Gasto): void {
    this.dialog.open(HistorialGastoDialogComponent, {
      width: '500px',
      data: { idGasto: gasto.idGasto, descripcion: gasto.descripcion }
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
