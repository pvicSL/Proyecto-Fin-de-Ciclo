import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvoiceMessage } from './invoice-message';

describe('InvoiceMessage', () => {
  let component: InvoiceMessage;
  let fixture: ComponentFixture<InvoiceMessage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvoiceMessage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InvoiceMessage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
