import { Component, OnInit, OnDestroy, ViewEncapsulation } from '@angular/core';
import { LayoutService } from '../../../../shared/services/layout.service';
import { AppointmentModify } from '../../../../shared/components/admin/appointment-modify/appointment-modify';
import { AppointmentService } from '../../../../core/services/appointment.service';

@Component({
  selector: 'app-generate-budget',
  imports: [AppointmentModify],
  templateUrl: './generate-budget.html',
  styleUrls: ['./generate-budget.css'],
  encapsulation: ViewEncapsulation.None
})
export class GenerateBudget implements OnInit, OnDestroy {
  constructor(
    private layoutService: LayoutService,
    private appointmentService: AppointmentService,
  ) {}

  ngOnInit(): void {
    this.layoutService.setShowParentButtons(false);
  }

  ngOnDestroy(): void {
    this.layoutService.setShowParentButtons(true);
  }

  rechazar(){
    this.appointmentService.deleteAppointment(1).subscribe({
    next: () => {
      console.log('Presupuesto rechazado');
    },
    error: (err) => {
      console.error('Error al rechazar', err);
    }
  });
  }
}
