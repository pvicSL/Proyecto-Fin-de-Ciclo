import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppointmentModifierComponent } from './appointment-modifier';

describe('AppointmentModifier', () => {
  let component: AppointmentModifierComponent;
  let fixture: ComponentFixture<AppointmentModifierComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppointmentModifierComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AppointmentModifierComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
