import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService, UsuarioService } from '../../../core/services';
import { AdminUsuario } from '../../../core/models';

@Component({
  selector: 'app-admin-usuarios',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatTableModule,
    MatMenuModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatFormFieldModule,
    MatSlideToggleModule,
    MatSnackBarModule
  ],
  templateUrl: './admin-usuarios.component.html',
  styleUrl: './admin-usuarios.component.css'
})
export class AdminUsuariosComponent implements OnInit {
  usuarios: AdminUsuario[] = [];
  loading = true;
  displayedColumns = ['nombre', 'email', 'departamento', 'rol', 'activo', 'acciones'];

  constructor(
    public authService: AuthService,
    private usuarioService: UsuarioService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.loading = true;
    this.usuarioService.obtenerTodosLosUsuariosAdmin().subscribe({
      next: (usuarios) => {
        this.usuarios = usuarios;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  cambiarRol(usuario: AdminUsuario, nuevoRol: string): void {
    this.usuarioService.actualizarUsuarioAdmin(usuario.idUsuario, { rol: nuevoRol }).subscribe({
      next: (updated) => {
        const idx = this.usuarios.findIndex(u => u.idUsuario === updated.idUsuario);
        if (idx !== -1) this.usuarios[idx] = updated;
        this.snackBar.open('Rol actualizado', 'OK', { duration: 3000 });
      },
      error: () => {
        this.snackBar.open('Error al actualizar rol', 'OK', { duration: 3000 });
      }
    });
  }

  toggleActivo(usuario: AdminUsuario): void {
    this.usuarioService.actualizarUsuarioAdmin(usuario.idUsuario, { activo: !usuario.activo }).subscribe({
      next: (updated) => {
        const idx = this.usuarios.findIndex(u => u.idUsuario === updated.idUsuario);
        if (idx !== -1) this.usuarios[idx] = updated;
        this.snackBar.open(
          updated.activo ? 'Usuario activado' : 'Usuario desactivado',
          'OK',
          { duration: 3000 }
        );
      },
      error: () => {
        this.snackBar.open('Error al cambiar estado', 'OK', { duration: 3000 });
      }
    });
  }

  volverAlDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getRolLabel(rol: string): string {
    switch (rol) {
      case 'ROLE_ADMIN': return 'Admin';
      case 'ROLE_MANAGER': return 'Manager';
      case 'ROLE_USER': return 'Usuario';
      default: return rol;
    }
  }

  getRolColor(rol: string): string {
    switch (rol) {
      case 'ROLE_ADMIN': return 'admin';
      case 'ROLE_MANAGER': return 'manager';
      default: return 'user';
    }
  }
}
