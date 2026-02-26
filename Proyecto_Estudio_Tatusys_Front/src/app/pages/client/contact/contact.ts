import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-contact',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './contact.html',
    styleUrl: './contact.css'
})
export class ContactComponent implements OnInit {

    // Variables de estado
    loading: boolean = false;
    envioExitoso: boolean = false;
    errorEnvio: boolean = false;

    // Modelo de datos del formulario
    formData = {
        nombre: '',
        telefono: '',
        email: '',
        mensaje: ''
    };

    // Estados de validación visual (true = válido, false = inválido, null = sin tocar)
    validStatus: any = {
        nombre: null,
        telefono: null,
        email: null,
        mensaje: null
    };

    ngOnInit(): void {
        console.log('[DEBUG] ContactComponent inicializado.');
    }

    // ==========================================
    // LÓGICA DE VALIDACIÓN EN TIEMPO REAL
    // ==========================================
    validarNombre() {
        const valor = this.formData.nombre.trim();
        // Permite letras, espacios, acentos y guiones (sin números ni símbolos raros)
        const regex = /^[a-zA-ZÀ-ÿ\u00f1\u00d1\s'-]+$/;

        if (valor.length >= 3 && regex.test(valor)) {
            this.validStatus.nombre = true;
        } else {
            this.validStatus.nombre = valor.length > 0 ? false : null;
        }
    }

    validarTelefono() {
        const valor = this.formData.telefono.trim();
        // Al ser opcional, si está vacío reseteamos la validación
        if (!valor) {
            this.validStatus.telefono = null;
            return;
        }
        // Verifica que solo contenga entre 9 y 15 números (formato internacional básico)
        const regex = /^[0-9]{9,15}$/;
        this.validStatus.telefono = regex.test(valor) ? true : false;
    }

    validarEmail() {
        const valor = this.formData.email.trim();
        // Regex estándar para validación de correos electrónicos
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (regex.test(valor)) {
            this.validStatus.email = true;
        } else {
            this.validStatus.email = valor.length > 0 ? false : null;
        }
    }

    validarMensaje() {
        const valor = this.formData.mensaje.trim();
        // Exigimos un mensaje con un mínimo de 10 caracteres
        if (valor.length >= 10) {
            this.validStatus.mensaje = true;
        } else {
            this.validStatus.mensaje = valor.length > 0 ? false : null;
        }
    }

    // Getter que determina si el botón de envío debe estar habilitado
    get isFormValid(): boolean {
        return this.validStatus.nombre === true &&
            this.validStatus.email === true &&
            this.validStatus.mensaje === true &&
            this.validStatus.telefono !== false;
    }

    // ==========================================
    // ENVÍO DE DATOS
    // ==========================================

    enviarMensaje() {
        if (!this.isFormValid) return;

        this.loading = true;
        this.errorEnvio = false;
        this.envioExitoso = false;

        console.log('[DEBUG] Datos listos para enviar a inkandcostudio@proton.me:', this.formData);

        // SIMULACIÓN DE LLAMADA AL BACKEND
        // TODO: Reemplazar por this.emailService.enviarContacto(this.formData).subscribe(...)
        setTimeout(() => {
            this.loading = false;
            this.envioExitoso = true;

            // Limpiamos el formulario tras el éxito
            this.formData = { nombre: '', telefono: '', email: '', mensaje: '' };
            this.validStatus = { nombre: null, telefono: null, email: null, mensaje: null };

        }, 1500);
    }
}