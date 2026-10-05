import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

// Validador personalizado: la edad debe ser igual o mayor a la mínima (mayoría de edad = 18)
export function edadMinima(minima: number = 18): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const valor = control.value;
    // Si está vacío, deja que Validators.required se encargue
    if (valor === null || valor === undefined || valor === '') {
      return null;
    }
    const edad = Number(valor);
    if (Number.isNaN(edad) || edad < minima) {
      return { edadMinima: { requerida: minima, actual: valor } };
    }
    return null;
  };
}
