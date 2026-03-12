import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'formatoHoras',
  standalone: true // Muy importante si tu componente es standalone
})
export class FormatoHorasPipe implements PipeTransform {

  transform(valor: number | string | null | undefined): string {
    if (valor === null || valor === undefined) return '--:--';

    if (typeof valor === 'number') {
      if (valor <= 0) return '0 min';

      const horas = Math.floor(valor / 60);
      const minsRestantes = valor % 60;

      if (horas === 0) return `${minsRestantes} min`;
      if (minsRestantes === 0) return `${horas}h`;

      return `${horas}h ${minsRestantes}min`;
    }

    const horaTexto = valor.trim();
    if (!horaTexto) return '--:--';

    const [horasRaw, minutosRaw] = horaTexto.split(':');
    const horas = Number.parseInt(horasRaw, 10);
    const minutos = Number.parseInt(minutosRaw, 10);

    if (Number.isNaN(horas) || Number.isNaN(minutos)) return '--:--';

    const hh = String(horas).padStart(2, '0');
    const mm = String(minutos).padStart(2, '0');
    return `${hh}:${mm}`;
  }
}