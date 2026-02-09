import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { AuthService, GastoService } from '../../../core/services';

@Component({
  selector: 'app-nuevo-gasto-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule
  ],
  templateUrl: './nuevo-gasto-dialog.component.html',
  styleUrl: './nuevo-gasto-dialog.component.css'
})
export class NuevoGastoDialogComponent {
  gastoForm: FormGroup;
  loading = false;
  errorMessage = '';

  monedas = ['EUR', 'USD', 'GBP', 'JPY', 'MXN'];

  imagePreview: string | null = null;
  selectedFileName = '';
  private imageBase64: string | null = null;

  private readonly MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

  constructor(
    private fb: FormBuilder,
    private gastoService: GastoService,
    private authService: AuthService,
    private dialogRef: MatDialogRef<NuevoGastoDialogComponent>
  ) {
    this.gastoForm = this.fb.group({
      descripcion: ['', [Validators.required, Validators.minLength(5)]],
      importeOriginal: ['', [Validators.required, Validators.min(0.01)]],
      monedaOriginal: ['EUR', Validators.required],
      fechaGasto: [new Date(), Validators.required]
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];

    if (file.size > this.MAX_FILE_SIZE) {
      this.errorMessage = 'La imagen no puede superar los 5MB';
      return;
    }

    if (!file.type.startsWith('image/')) {
      this.errorMessage = 'Solo se permiten archivos de imagen';
      return;
    }

    this.errorMessage = '';
    this.selectedFileName = file.name;

    const reader = new FileReader();
    reader.onload = () => {
      const result = reader.result as string;
      this.imagePreview = result;
      this.imageBase64 = result;
    };
    reader.readAsDataURL(file);
  }

  removeImage(event: Event): void {
    event.stopPropagation();
    this.imagePreview = null;
    this.imageBase64 = null;
    this.selectedFileName = '';
  }

  onSubmit(): void {
    if (this.gastoForm.invalid) {
      return;
    }

    const currentUser = this.authService.currentUserValue;
    if (!currentUser) return;

    this.loading = true;
    this.errorMessage = '';

    const formValue = this.gastoForm.value;
    const gasto: any = {
      ...formValue,
      fechaGasto: this.formatDate(formValue.fechaGasto)
    };

    if (this.imageBase64) {
      gasto.imagenTicket = this.imageBase64;
    }

    this.gastoService.crearGasto(currentUser.idUsuario, gasto).subscribe({
      next: () => {
        this.dialogRef.close(true);
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Error al crear el gasto';
      }
    });
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
