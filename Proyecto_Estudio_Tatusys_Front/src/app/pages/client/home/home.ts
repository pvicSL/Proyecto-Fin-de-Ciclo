import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookingFormComponent } from './booking-form/booking-form';
import { AppointmentModifierComponent } from './appointment-modifier/appointment-modifier';

// Interface que sirve de molde para las preguntas frecuentes
// Así, cada FAQ tiene que tener texto para la pregunta y para la respuesta.
interface FaqElement {
  question: string;
  answer: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, BookingFormComponent, AppointmentModifierComponent],
  templateUrl: './home.html',
  styleUrl: './home.css'
})


export class HomeComponent {
  // 'viewMode' sirve para alternar entre la solicitud de cita o la modificación.
  // Y que no se vean las dos a la vez, para no confundir al usuario.
  viewMode: 'booking' | 'modifying' = 'booking';

  // Array de las preguntas frecuentes. Sigue lo que le marca el interface.
  // Esto evita incluir el contenido en el html, por lo que hace el diseño más mantenible.
  faqs: FaqElement[] = [
    {
      question: '¿Duele hacerse un tatuaje?',
      answer: 'Depende de la zona y la tolerancia personal, pero la mayoría de las personas describen la sensación como molestia más que dolor agudo. En el estudio disponemos de cremas anestesiantes y podemos hacer los descansos que sean necesarios para adaptarnos a tu nivel de tolerancia, así que puedes tener la tranquilidad de que nos esforzamos porque tu experiencia sea lo más agradable posible.'
    },
    {
      question: '¿Qué cuidados son necesarios cuando el tatuaje esté terminado?',
      answer: 'Cuidar de tu nuevo tatuaje es muy fácil: mantén la zona limpia, aplica la pomada recomendada, evita exponer tu piel al sol directo y no rasques la costra. Si notas irritación, sangrado, pus o molestias que no desaparecen, acude a tu médico.'
    },
    {
      question: '¿Puedo enviar referencias de tatuajes que me gustan?',
      answer: 'Sí, puedes subir hasta 3 imágenes (que pesen 5MB como máximo) como referencia al solicitar la cita.'
    },
    {
      question: '¿Qué es la densidad de tinta?',
      answer: 'La densidad se refiere a la saturación del pigmento en la piel. Dependiendo del estilo y el diseño, aplicamos diferentes densidades. Un diseño con mucho detalle o grandes bloques sólidos llevará más tinta que uno en el que las líneas no sean del todo opacas, por ejemplo.'
    },
    {
      question: '¿Qué estilos ofrecéis en el estudio?',
      answer: 'Somos especialistas en diseños de Realismo, Tradicional (Old School), Japonés, Lettering, Fineline, Black and Grey y Anime.'
    }
  ];

  toggleMode() {
    // Esta función alterna entre los dos estados,
    // de modo que podamos ocultar elementos según qué esté haciendo el usuario.
    this.viewMode = this.viewMode === 'booking' ? 'modifying' : 'booking';

    // Verificación por consola del modo que está activo
    console.log('Modo actual de visualización:', this.viewMode);
  }
}