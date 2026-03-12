import { FormatoHorasPipe } from './formato-horas-pipe';

describe('FormatoHorasPipe', () => {
  const pipe = new FormatoHorasPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('debe formatear hora HH:mm:ss a HH:MM', () => {
    expect(pipe.transform('9:5:00')).toBe('09:05');
    expect(pipe.transform('14:30:00')).toBe('14:30');
  });

  it('debe mantener formato de duración para números', () => {
    expect(pipe.transform(90)).toBe('1h 30min');
  });
});
