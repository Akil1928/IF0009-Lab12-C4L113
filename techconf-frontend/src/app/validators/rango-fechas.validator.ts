import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

// Validador cruzado para asegurar coherencia de fechas
export const validarRangoFechas: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const inicio = control.get('fechaInicio')?.value;
  const fin = control.get('fechaFin')?.value;

  if (inicio && fin) {
    const dInicio = new Date(inicio);
    const dFin = new Date(fin);
    if (dFin < dInicio) {
      return { fechasInvalidas: true };
    }
  }
  return null;
};
