import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PricesSettings } from './prices-settings';

describe('PricesSettings', () => {
  let component: PricesSettings;
  let fixture: ComponentFixture<PricesSettings>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PricesSettings]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PricesSettings);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
