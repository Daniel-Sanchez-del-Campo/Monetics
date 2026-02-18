import { Component, OnInit } from '@angular/core';
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
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { forkJoin, of, catchError } from 'rxjs';
import { AuthService, GastoService, CategoriaService, TicketService } from '../../../core/services';
import { Categoria, DriveUploadResponse, AnalisisTicketResponse } from '../../../core/models';

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
    MatIconModule,
    MatProgressBarModule,
    MatTooltipModule
  ],
  templateUrl: './nuevo-gasto-dialog.component.html',
  styleUrl: './nuevo-gasto-dialog.component.css'
})
export class NuevoGastoDialogComponent implements OnInit {
  gastoForm: FormGroup;
  loading = false;
  errorMessage = '';

  monedas = ['EUR', 'USD', 'GBP', 'JPY', 'MXN'];
  categorias: Categoria[] = [];

  // Imagen y Drive
  imagePreview: string | null = null;
  selectedFileName = '';
  private selectedFile: File | null = null;
  private driveFileId: string | null = null;
  private driveFileUrl: string | null = null;

  // IA
  analizando = false;
  analisisCompletado = false;
  analizadoPorIa = false;
  iaConfianza = 0;
  confianzaPorCampo: { [key: string]: number } = {};
  mensajeProgreso = '';

  private readonly MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

  constructor(
    private fb: FormBuilder,
    private gastoService: GastoService,
    private authService: AuthService,
    private categoriaService: CategoriaService,
    private ticketService: TicketService,
    private dialogRef: MatDialogRef<NuevoGastoDialogComponent>
  ) {
    this.gastoForm = this.fb.group({
      descripcion: ['', [Validators.required, Validators.minLength(5)]],
      importeOriginal: ['', [Validators.required, Validators.min(0.01)]],
      monedaOriginal: ['EUR', Validators.required],
      fechaGasto: [new Date(), Validators.required],
      idCategoria: [null]
    });
  }

  ngOnInit(): void {
    this.categoriaService.obtenerActivas().subscribe({
      next: (cats) => this.categorias = cats,
      error: () => {}
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
    this.selectedFile = file;

    // Mostrar preview local inmediatamente
    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result as string;
    };
    reader.readAsDataURL(file);

    // Lanzar subida a Drive + analisis IA en paralelo
    this.procesarImagen(file);
  }

  /**
   * Sube la imagen a Drive y la analiza con IA en paralelo.
   * Cada llamada es independiente: si Drive falla, el analisis IA sigue funcionando.
   */
  private procesarImagen(file: File): void {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser) return;

    this.analizando = true;
    this.analisisCompletado = false;
    this.mensajeProgreso = 'Analizando ticket con IA...';

    // Lanzar ambas llamadas en paralelo, cada una con su propio catchError
    forkJoin({
      drive: this.ticketService.subirImagen(file, currentUser.idUsuario).pipe(
        catchError(() => of(null))
      ),
      analisis: this.ticketService.analizarTicket(file).pipe(
        catchError(() => of(null))
      )
    }).subscribe({
      next: (result) => {
        // Guardar datos de Drive (si funciono)
        if (result.drive) {
          this.driveFileId = result.drive.driveFileId;
          this.driveFileUrl = result.drive.driveFileUrl;
        }

        // Prerrellenar formulario con datos de la IA (si funciono)
        if (result.analisis) {
          this.aplicarAnalisis(result.analisis);
          this.analisisCompletado = true;
        } else {
          this.errorMessage = 'No se pudo analizar el ticket. Rellena los datos manualmente.';
        }

        this.analizando = false;
        this.mensajeProgreso = '';
      }
    });
  }

  /**
   * Aplica los datos extraidos por la IA al formulario.
   */
  private aplicarAnalisis(analisis: AnalisisTicketResponse): void {
    this.analizadoPorIa = true;
    this.iaConfianza = analisis.confianza;
    this.confianzaPorCampo = analisis.confianzaPorCampo;

    // Solo prerrellenar si hay datos con confianza razonable
    if (analisis.descripcion && analisis.confianzaPorCampo.descripcion > 0.3) {
      this.gastoForm.patchValue({ descripcion: analisis.descripcion });
    }

    if (analisis.importeOriginal && analisis.confianzaPorCampo.importe > 0.3) {
      this.gastoForm.patchValue({ importeOriginal: analisis.importeOriginal });
    }

    if (analisis.monedaOriginal && analisis.confianzaPorCampo.moneda > 0.3) {
      this.gastoForm.patchValue({ monedaOriginal: analisis.monedaOriginal });
    }

    if (analisis.fechaGasto && analisis.confianzaPorCampo.fecha > 0.3) {
      this.gastoForm.patchValue({ fechaGasto: new Date(analisis.fechaGasto) });
    }

    if (analisis.idCategoriaSugerida && analisis.confianzaPorCampo.categoria > 0.3) {
      this.gastoForm.patchValue({ idCategoria: analisis.idCategoriaSugerida });
    }
  }

  /**
   * Devuelve la clase CSS del indicador de confianza para un campo.
   */
  getConfianzaClass(campo: string): string {
    if (!this.analisisCompletado || !this.confianzaPorCampo[campo]) return '';
    const valor = this.confianzaPorCampo[campo];
    if (valor >= 0.8) return 'confianza-alta';
    if (valor >= 0.5) return 'confianza-media';
    return 'confianza-baja';
  }

  /**
   * Devuelve el tooltip de confianza para un campo.
   */
  getConfianzaTooltip(campo: string): string {
    if (!this.analisisCompletado || !this.confianzaPorCampo[campo]) return '';
    const porcentaje = Math.round(this.confianzaPorCampo[campo] * 100);
    return `Confianza de la IA: ${porcentaje}%`;
  }

  removeImage(event: Event): void {
    event.stopPropagation();

    // Si ya se subio a Drive, eliminarlo
    if (this.driveFileId) {
      this.ticketService.eliminarImagen(this.driveFileId).subscribe();
    }

    this.imagePreview = null;
    this.selectedFile = null;
    this.selectedFileName = '';
    this.driveFileId = null;
    this.driveFileUrl = null;
    this.analisisCompletado = false;
    this.analizadoPorIa = false;
    this.confianzaPorCampo = {};
  }

  onSubmit(): void {
    if (this.gastoForm.invalid) return;

    const currentUser = this.authService.currentUserValue;
    if (!currentUser) return;

    this.loading = true;
    this.errorMessage = '';

    const formValue = this.gastoForm.value;
    const gasto: any = {
      ...formValue,
      fechaGasto: this.formatDate(formValue.fechaGasto)
    };

    // Campos de Drive
    if (this.driveFileId) {
      gasto.driveFileId = this.driveFileId;
      gasto.driveFileUrl = this.driveFileUrl;
      gasto.imagenNombre = this.selectedFileName;
    }

    // Campos de IA
    if (this.analizadoPorIa) {
      gasto.analizadoPorIa = true;
      gasto.iaConfianza = this.iaConfianza;
    }

    if (!gasto.idCategoria) {
      delete gasto.idCategoria;
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
    // Si se subio imagen a Drive pero se cancela, eliminarla
    if (this.driveFileId) {
      this.ticketService.eliminarImagen(this.driveFileId).subscribe();
    }
    this.dialogRef.close(false);
  }
}
