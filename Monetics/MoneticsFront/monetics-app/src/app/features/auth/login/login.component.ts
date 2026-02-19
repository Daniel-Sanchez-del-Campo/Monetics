import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../../core/services';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';
  hidePassword = true;
  showForm = false;

  features = [
    { icon: 'receipt_long', title: 'Tickets con justificante', desc: 'Registra gastos con foto del ticket y conversion automatica de moneda.' },
    { icon: 'verified', title: 'Flujo de aprobacion', desc: 'Estados de aprobacion con trazabilidad completa de cada gasto.' },
    { icon: 'dashboard', title: 'Dashboards inteligentes', desc: 'Visualiza gastos en tiempo real con graficos y alertas de presupuesto.' },
    { icon: 'account_balance', title: 'Control presupuestario', desc: 'Gestiona presupuestos por departamento con alertas automaticas.' }
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  abrirLogin(): void {
    this.showForm = true;
    this.errorMessage = '';
    this.loginForm.reset();
  }

  cerrarLogin(): void {
    this.showForm = false;
    this.errorMessage = '';
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Error al iniciar sesion. Verifica tus credenciales.';
      }
    });
  }

  get emailControl() {
    return this.loginForm.get('email');
  }

  get passwordControl() {
    return this.loginForm.get('password');
  }
}
