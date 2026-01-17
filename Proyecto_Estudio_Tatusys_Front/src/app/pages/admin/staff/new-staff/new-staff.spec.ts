import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewStaff } from './new-staff';

describe('NewStaff', () => {
  let component: NewStaff;
  let fixture: ComponentFixture<NewStaff>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewStaff]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewStaff);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
