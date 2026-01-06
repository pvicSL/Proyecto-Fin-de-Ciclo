import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppointmentConfirm } from './appointment-confirm';

describe('AppointmentConfirm', () => {
  let component: AppointmentConfirm;
  let fixture: ComponentFixture<AppointmentConfirm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppointmentConfirm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppointmentConfirm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
