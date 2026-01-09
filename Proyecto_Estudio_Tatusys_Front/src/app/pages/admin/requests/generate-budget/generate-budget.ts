import { Component, OnInit, OnDestroy, ViewEncapsulation } from '@angular/core';
import { LayoutService } from '../../../../shared/services/layout.service';
import { AppointmentModify } from '../../../../shared/components/admin/appointment-modify/appointment-modify';

@Component({
  selector: 'app-generate-budget',
  imports: [AppointmentModify],
  templateUrl: './generate-budget.html',
  styleUrls: ['./generate-budget.css'],
  encapsulation: ViewEncapsulation.None
})
export class GenerateBudget implements OnInit, OnDestroy {
  constructor(private layoutService: LayoutService) {}

  ngOnInit(): void {
    this.layoutService.setShowParentButtons(false);
  }

  ngOnDestroy(): void {
    this.layoutService.setShowParentButtons(true);
  }
}
