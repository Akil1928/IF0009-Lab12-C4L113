import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormArray, Validators } from '@angular/forms';
import { CharlaService, Charla, Asistente } from '../../services/charla.service';
import { AsistenteFormComponent } from '../asistente-form/asistente-form.component';
import { validarRangoFechas } from '../../validators/rango-fechas.validator';

@Component({
  selector: 'app-charla-registro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, AsistenteFormComponent],
  templateUrl: './charla-registro.component.html',
  styleUrl: './charla-registro.component.css'
})
export class CharlaRegistroComponent implements OnInit {
  private charlaService = inject(CharlaService);
  private fb = inject(FormBuilder);
  charlas: Charla[] = [];
  mensajeExito: string = '';
  mensajeAsistente: string = '';
  charlaAbiertaId: number | null = null; // Tarjeta con el formulario de inscripción desplegado

  // Formulario con fechas, validador cruzado a nivel de grupo y FormArray de etiquetas
  registroForm = this.fb.group({
    titulo: ['', [Validators.required, Validators.minLength(5)]],
    expositor: ['', [Validators.required]],
    nivel: ['Principiante', [Validators.required]],
    emailContacto: ['', [Validators.required, Validators.email]],
    fechaInicio: ['', [Validators.required]],
    fechaFin: ['', [Validators.required]],
    etiquetas: this.fb.array([
      this.fb.control('', Validators.required)
    ])
  }, { validators: validarRangoFechas });

  ngOnInit(): void {
    this.cargarCharlas();
  }

  cargarCharlas() {
    this.charlaService.getCharlas().subscribe({
      next: (data) => this.charlas = data,
      error: (err) => console.error('Error al cargar las charlas', err)
    });
  }

  onSubmit(): void {
    if (this.registroForm.invalid) {
      this.registroForm.markAllAsTouched();
      return;
    }

    const nuevaCharla = this.registroForm.getRawValue() as Charla;
    this.charlaService.registrarCharla(nuevaCharla).subscribe({
      next: (res) => {
        this.mensajeExito = '¡Charla registrada exitosamente!';
        this.charlas = [...this.charlas, res];
        this.reiniciarFormulario();
      },
      error: (err) => console.error(err)
    });
  }

  // Despliega u oculta el formulario de inscripción de una tarjeta
  toggleInscripcion(charlaId: number | undefined): void {
    if (charlaId === undefined) { return; }
    this.charlaAbiertaId = this.charlaAbiertaId === charlaId ? null : charlaId;
  }

  // Actualiza la tarjeta de la charla con el nuevo asistente devuelto por el servidor
  onAsistenteInscrito(charlaId: number | undefined, asistente: Asistente): void {
    this.charlas = this.charlas.map(c =>
      c.id === charlaId ? { ...c, asistentes: [...(c.asistentes ?? []), asistente] } : c
    );
    this.charlaAbiertaId = null;
    this.mensajeAsistente = `¡${asistente.nombreCompleto} fue inscrito exitosamente!`;
  }

  private reiniciarFormulario(): void {
    // Dejar el FormArray con un solo input vacío antes del reset
    this.etiquetasArray.clear();
    this.etiquetasArray.push(this.fb.control('', Validators.required));
    this.registroForm.reset({ nivel: 'Principiante' });
  }

  // Getters auxiliares
  get tituloCtrl() { return this.registroForm.get('titulo'); }
  get expositorCtrl() { return this.registroForm.get('expositor'); }
  get emailCtrl() { return this.registroForm.get('emailContacto'); }

  get etiquetasArray() {
    return this.registroForm.get('etiquetas') as FormArray;
  }

  agregarEtiqueta() {
    this.etiquetasArray.push(this.fb.control('', Validators.required));
  }

  removerEtiqueta(index: number) {
    if (this.etiquetasArray.length > 1) {
      this.etiquetasArray.removeAt(index);
    }
  }
}
