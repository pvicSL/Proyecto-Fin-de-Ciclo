import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListStaff } from './list-staff';

describe('ListStaff', () => {
  let component: ListStaff;
  let fixture: ComponentFixture<ListStaff>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListStaff]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListStaff);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
