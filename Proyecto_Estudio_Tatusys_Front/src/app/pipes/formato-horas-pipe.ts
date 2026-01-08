import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'formatoHoras',
  standalone: true // Muy importante si tu componente es standalone
})
export class FormatoHorasPipe implements PipeTransform {

  transform(minutos: number | null | undefined): string {
    if (!minutos || minutos <= 0) return '0 min';

    const horas = Math.floor(minutos / 60);
    const minsRestantes = minutos % 60;

    if (horas === 0) return `${minsRestantes} min`;
    if (minsRestantes === 0) return `${horas}h`;
    
    return `${horas}h ${minsRestantes}min`;
  }
}