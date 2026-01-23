import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { PaymentService } from '../../../../core/services/payment.service';

@Component({
  selector: 'app-payment-gateway',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './payment-gateway.html',
  styleUrls: ['./payment-gateway.css']
})
export class PaymentGatewayComponent implements OnInit {

  token: string = '';
  loading: boolean = true;
  tokenValido: boolean = false;
  pagoRealizado: boolean = false;
  presupuestoRechazado: boolean = false;
  errorMsg: string = '';

  datosCita: any = {};

  tarjeta: any = {
    nombre: '',
    numero: '',
    expiracion: '',
    cvc: ''
  };

  // Objeto para controlar el estado visual de cada campo (true=valido, false=invalido, null=sin tocar)
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
    private paymentService: PaymentService
  ) { }

  ngOnInit(): void {
    // MODO PRUEBA VISUAL
    this.token = this.route.snapshot.paramMap.get('token') || '';
    this.loading = false;
    this.tokenValido = true;
    this.datosCita = {
      referencia: 'CITA-TEST-2026',
      precioTotal: 250.00,
      fianza: 30,
      fecha: '22-03-2026',
      hora: '16:30'
    };
  }

  // ==========================================
  // LÓGICA DE FORMATEO Y VALIDACIÓN EN TIEMPO REAL
  // ==========================================

  // 1. VALIDAR NOMBRE: Solo letras, espacios, guiones y tildes
  validarNombre() {
    const valor = this.tarjeta.nombre;
    // Regex: Permite letras a-z, acentos, ñ, guiones y apostrofes. NO números ni @.
    const regexNombre = /^[a-zA-ZÀ-ÿ\u00f1\u00d1\s'-]+$/;

    if (valor && valor.length >= 3 && regexNombre.test(valor)) {
      this.validStatus.nombre = true;
    } else {
      this.validStatus.nombre = valor ? false : null; // Si está vacío vuelve a neutro
    }
  }

  // 2. FORMATEAR Y VALIDAR NÚMERO DE TARJETA (Luhn + Longitud)
  formatearTarjeta(event: any) {
    let input = event.target.value.replace(/\D/g, ''); // Quitar todo lo que no sea número
    // Añadir espacio cada 4 dígitos
    input = input.substring(0, 19); // Máximo 19 dígitos
    const formatted = input.match(/.{1,4}/g)?.join(' ') || '';
    this.tarjeta.numero = formatted;

    // Validar mientras escribe si ya tiene longitud mínima
    if (input.length >= 14) {
      this.validarTarjeta();
    } else {
      this.validStatus.numero = null;
    }
  }

  validarTarjeta() {
    // Quitamos espacios para validar
    const numeroLimpio = this.tarjeta.numero.replace(/\s/g, '');

    // Longitud entre 14 y 19
    const longitudValida = numeroLimpio.length >= 14 && numeroLimpio.length <= 19;

    // Algoritmo de Luhn (Check digit)
    const luhnValido = this.luhnCheck(numeroLimpio);

    this.validStatus.numero = longitudValida && luhnValido;
  }

  // Algoritmo de Luhn estándar
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

  // 3. FORMATEAR Y VALIDAR FECHA (MM/AA)
  formatearFecha(event: any) {
    let input = event.target.value.replace(/\D/g, ''); // Solo números

    // LÓGICA DE AUTO-COMPLETADO DE MES
    // Si escribe un número del 2 al 9 como primer dígito, asumimos que es 02, 03...
    if (input.length === 1 && parseInt(input) > 1) {
      input = '0' + input;
    }

    // Insertar barra automáticamente
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
    // Ajuste para evitar problemas de mes vencido en el mismo año
    const fechaActual = new Date(ahora.getFullYear(), ahora.getMonth());
    const fechaTarjeta = new Date(anio, mes - 1); // Mes en JS es 0-11

    // Si la fecha de la tarjeta es anterior a este mes, error
    // Nota: A veces se permite el mes actual, aquí somos estrictos > hoy
    if (fechaTarjeta < fechaActual) {
      this.validStatus.expiracion = false;
    } else {
      this.validStatus.expiracion = true;
    }
  }

  // 4. VALIDAR CVC
  validarCvc() {
    const valor = this.tarjeta.cvc.replace(/\D/g, '');
    this.tarjeta.cvc = valor;

    // Resetear error específico
    this.cvcCorto = false;

    if (valor.length >= 3 && valor.length <= 4) {
      this.validStatus.cvc = true;
    } else {
      this.validStatus.cvc = valor.length > 0 ? false : null;
      // Si hay algo escrito pero es corto, activamos el flag
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
    setTimeout(() => {
      this.loading = false;
      this.pagoRealizado = true;
    }, 2000);
  }

  rechazarPresupuesto() {
    const confirmar = window.confirm("¿Estás seguro de que deseas RECHAZAR la pre-reserva? Tu solicitud quedará cancelada.");
    if (confirmar) {
      this.loading = true;
      setTimeout(() => {
        this.loading = false;
        this.presupuestoRechazado = true;
      }, 1500);
    }
  }
}