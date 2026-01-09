import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerateBudget } from './generate-budget';

describe('GenerateBudget', () => {
  let component: GenerateBudget;
  let fixture: ComponentFixture<GenerateBudget>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenerateBudget]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GenerateBudget);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
