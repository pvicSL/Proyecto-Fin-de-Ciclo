import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServicesSettings } from './services-settings';

describe('ServicesSettings', () => {
  let component: ServicesSettings;
  let fixture: ComponentFixture<ServicesSettings>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServicesSettings]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServicesSettings);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
