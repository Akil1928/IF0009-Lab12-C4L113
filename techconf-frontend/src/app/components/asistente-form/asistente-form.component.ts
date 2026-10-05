import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Asistente, CharlaService } from '../../services/charla.service';
import { edadMinima } from '../../validators/edad-minima.validator';

@Component({
  selector: 'app-asistente-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './asistente-form.component.html',
  styleUrl: './asistente-form.component.css'
})
export class AsistenteFormComponent {
  private fb = inject(FormBuilder);
  private charlaService = inject(CharlaService);

  @Input({ required: true }) charlaId!: number;
  @Output() inscrito = new EventEmitter<Asistente>();
  @Output() cancelado = new EventEmitter<void>();

  errorServidor = '';
  enviando = false;

  // Formulario anidado: FormGroup raíz que contiene un FormGroup "asistente"
  form = this.fb.group({
    asistente: this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3)]],
      correo: ['', [Validators.required, Validators.email]],
      edad: [null as number | null, [Validators.required, edadMinima(18)]]
    })
  });

  get asistenteGroup() { return this.form.controls.asistente; }
  get nombre() { return this.asistenteGroup.controls.nombre; }
  get correo() { return this.asistenteGroup.controls.correo; }
  get edad() { return this.asistenteGroup.controls.edad; }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const v = this.asistenteGroup.getRawValue();
    const payload: Asistente = {
      nombreCompleto: (v.nombre ?? '').trim(),
      correo: (v.correo ?? '').trim(),
      edad: Number(v.edad)
    };

    this.enviando = true;
    this.errorServidor = '';
    this.charlaService.inscribirAsistente(this.charlaId, payload).subscribe({
      next: (asistente) => {
        this.enviando = false;
        this.inscrito.emit(asistente);
        this.form.reset();
      },
      error: (err: HttpErrorResponse) => {
        this.enviando = false;
        this.errorServidor = this.extraerMensaje(err);
      }
    });
  }

  // Convierte la respuesta de error del servidor (400/404/409) en texto para el usuario
  private extraerMensaje(err: HttpErrorResponse): string {
    const errores = err.error?.errores;
    if (errores) {
      return Object.values(errores).join(' ');
    }
    return err.error?.message || err.error?.mensaje || 'No se pudo inscribir al asistente.';
  }
}
