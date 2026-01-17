import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteStaff } from './delete-staff';

describe('DeleteStaff', () => {
  let component: DeleteStaff;
  let fixture: ComponentFixture<DeleteStaff>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteStaff]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeleteStaff);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
