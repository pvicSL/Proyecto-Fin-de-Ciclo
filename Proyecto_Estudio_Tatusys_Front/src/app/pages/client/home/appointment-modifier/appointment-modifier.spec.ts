import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppointmentModifier } from './appointment-modifier';

describe('AppointmentModifier', () => {
  let component: AppointmentModifier;
  let fixture: ComponentFixture<AppointmentModifier>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppointmentModifier]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppointmentModifier);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
