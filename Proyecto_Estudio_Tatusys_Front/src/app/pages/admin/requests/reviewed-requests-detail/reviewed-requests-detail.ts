import { Component, OnInit, OnDestroy, ViewEncapsulation } from '@angular/core';
import { AppointmentDetails } from '../../../../shared/components/admin/appointment-details/appointment-details';
import { LayoutService } from '../../../../shared/services/layout.service';

@Component({
  selector: 'app-reviewed-requests-detail',
  imports: [AppointmentDetails],
  templateUrl: './reviewed-requests-detail.html',
  styleUrls: ['./reviewed-requests-detail.css'],
  encapsulation: ViewEncapsulation.None
})
export class ReviewedRequestsDetail implements OnInit, OnDestroy {
  constructor(private layoutService: LayoutService) {}

  ngOnInit(): void {
    // Oculta los botones del padre al entrar
    this.layoutService.setShowParentButtons(false);
  }

  ngOnDestroy(): void {
    // Restaura la visibilidad al salir
    this.layoutService.setShowParentButtons(true);
  }
}
