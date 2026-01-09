import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppointmentModify } from './appointment-modify';

describe('AppointmentModify', () => {
  let component: AppointmentModify;
  let fixture: ComponentFixture<AppointmentModify>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppointmentModify]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppointmentModify);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
