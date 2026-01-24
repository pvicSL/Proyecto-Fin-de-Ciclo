import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
// He quitado PaymentService porque ya no lo usas aquí, usas AppointmentService
import { AppointmentService } from '../../../../core/services/appointment.service';

@Component({
  selector: 'app-payment-gateway',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './payment-gateway.html',
  styleUrls: ['./payment-gateway.css']
})
export class PaymentGatewayComponent implements OnInit {

  // Variables de Estado
  loading: boolean = true;      // Empieza cargando
  tokenValido: boolean = false; // Falso hasta que verifiquemos la cita
  pagoRealizado: boolean = false;
  citaYaPagada: boolean = false;
  presupuestoRechazado: boolean = false; // <--- ¡AQUÍ ESTABA EL ERROR! Faltaba esta línea
  errorMsg: string = '';

  // Datos de la cita
  datosCita: any = null;

  tarjeta: any = {
    nombre: '',
    numero: '',
    expiracion: '',
    cvc: ''
  };

  // Objeto para controlar el estado visual de cada campo
  validStatus: any = {
    nombre: null,
    numero: null,
    expiracion: null,
    cvc: null
  };

  cvcCorto: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private appointmentService: AppointmentService
  ) { }

  ngOnInit(): void {
    // 1. Capturamos la referencia que viene en la URL
    const referencia = this.route.snapshot.paramMap.get('token');

    if (!referencia) {
      this.errorMsg = "No se ha proporcionado una referencia de cita.";
      this.loading = false;
      return;
    }

    // 2. Llamamos al Backend para buscar la cita REAL
    this.appointmentService.getPublicAppointmentByRef(referencia).subscribe({
      next: (data) => {
        this.datosCita = data; // Guardamos los datos reales
        this.loading = false;

        // 3. Comprobaciones de seguridad
        if (data.estatus === 'CONFIRMADO') {
          this.citaYaPagada = true; // Bloqueamos si ya pagó
          return;
        }

        // Si llegamos aquí, todo es correcto y mostramos el formulario
        this.tokenValido = true;
      },
      error: (err) => {
        this.loading = false;
        // Si el back devuelve 404 (No existe) o 410 (Caducado)
        if (err.status === 410 || (err.error && err.error.includes('caducado'))) {
          this.errorMsg = "El enlace de pago ha caducado (48h expiradas).";
        } else {
          this.errorMsg = "No hemos encontrado la cita o el enlace es incorrecto.";
        }
      }
    });
  }

  // ==========================================
  // LÓGICA DE FORMATEO Y VALIDACIÓN EN TIEMPO REAL
  // ==========================================

  validarNombre() {
    const valor = this.tarjeta.nombre;
    const regexNombre = /^[a-zA-ZÀ-ÿ\u00f1\u00d1\s'-]+$/;

    if (valor && valor.length >= 3 && regexNombre.test(valor)) {
      this.validStatus.nombre = true;
    } else {
      this.validStatus.nombre = valor ? false : null;
    }
  }

  formatearTarjeta(event: any) {
    let input = event.target.value.replace(/\D/g, '');
    input = input.substring(0, 19);
    const formatted = input.match(/.{1,4}/g)?.join(' ') || '';
    this.tarjeta.numero = formatted;

    if (input.length >= 14) {
      this.validarTarjeta();
    } else {
      this.validStatus.numero = null;
    }
  }

  validarTarjeta() {
    const numeroLimpio = this.tarjeta.numero.replace(/\s/g, '');
    const longitudValida = numeroLimpio.length >= 14 && numeroLimpio.length <= 19;
    const luhnValido = this.luhnCheck(numeroLimpio);
    this.validStatus.numero = longitudValida && luhnValido;
  }

  luhnCheck(val: string): boolean {
    let sum = 0;
    let shouldDouble = false;
    for (let i = val.length - 1; i >= 0; i--) {
      let digit = parseInt(val.charAt(i));
      if (shouldDouble) {
        if ((digit *= 2) > 9) digit -= 9;
      }
      sum += digit;
      shouldDouble = !shouldDouble;
    }
    return (sum % 10) == 0;
  }

  formatearFecha(event: any) {
    let input = event.target.value.replace(/\D/g, '');

    if (input.length === 1 && parseInt(input) > 1) {
      input = '0' + input;
    }

    if (input.length >= 2) {
      input = input.substring(0, 2) + '/' + input.substring(2, 4);
    }

    this.tarjeta.expiracion = input;

    if (input.length === 5) {
      this.validarFecha();
    } else {
      this.validStatus.expiracion = null;
    }
  }

  validarFecha() {
    const valor = this.tarjeta.expiracion;
    const regexFecha = /^(0[1-9]|1[0-2]|[1-9])\/?([0-9]{2})$/;

    if (!regexFecha.test(valor)) {
      this.validStatus.expiracion = false;
      return;
    }

    const partes = valor.split('/');
    const mes = parseInt(partes[0], 10);
    const anio = parseInt('20' + partes[1], 10);

    const ahora = new Date();
    const fechaActual = new Date(ahora.getFullYear(), ahora.getMonth());
    const fechaTarjeta = new Date(anio, mes - 1);

    if (fechaTarjeta < fechaActual) {
      this.validStatus.expiracion = false;
    } else {
      this.validStatus.expiracion = true;
    }
  }

  validarCvc() {
    const valor = this.tarjeta.cvc.replace(/\D/g, '');
    this.tarjeta.cvc = valor;
    this.cvcCorto = false;

    if (valor.length >= 3 && valor.length <= 4) {
      this.validStatus.cvc = true;
    } else {
      this.validStatus.cvc = valor.length > 0 ? false : null;
      if (valor.length > 0 && valor.length < 3) {
        this.cvcCorto = true;
      }
    }
  }

  get isFormValid(): boolean {
    return this.validStatus.nombre === true &&
      this.validStatus.numero === true &&
      this.validStatus.expiracion === true &&
      this.validStatus.cvc === true;
  }

  realizarPago() {
    if (!this.isFormValid) return;

    this.loading = true;

    // LLAMADA AL BACKEND REAL
    this.appointmentService.confirmPayment(this.datosCita.referencia).subscribe({
      next: (resp) => {
        setTimeout(() => {
          this.loading = false;
          this.pagoRealizado = true;
        }, 1500);
      },
      error: (err) => {
        this.loading = false;
        alert(err.error || "Hubo un error al procesar el pago. Inténtalo de nuevo.");
      }
    });
  }

  rechazarPresupuesto() {
    const confirmar = window.confirm("¿Estás seguro de que deseas RECHAZAR la pre-reserva? Tu solicitud quedará cancelada.");
    if (confirmar) {
      this.loading = true;
      // NOTA: Aquí solo estamos simulando visualmente.
      // Si quisieras cancelar realmente en backend, deberías llamar a this.appointmentService.cancelAppointment(...)
      // Pero para el alcance actual del pago, esto vale.
      setTimeout(() => {
        this.loading = false;
        this.presupuestoRechazado = true; // Ahora sí funcionará
      }, 1500);
    }
  }
}