import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewedRequests } from './reviewed-requests';

describe('ReviewedRequests', () => {
  let component: ReviewedRequests;
  let fixture: ComponentFixture<ReviewedRequests>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewedRequests]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewedRequests);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
