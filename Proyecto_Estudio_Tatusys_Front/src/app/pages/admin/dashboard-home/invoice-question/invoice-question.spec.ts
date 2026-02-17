import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvoiceQuestion } from './invoice-question';

describe('InvoiceQuestion', () => {
  let component: InvoiceQuestion;
  let fixture: ComponentFixture<InvoiceQuestion>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvoiceQuestion]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InvoiceQuestion);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
