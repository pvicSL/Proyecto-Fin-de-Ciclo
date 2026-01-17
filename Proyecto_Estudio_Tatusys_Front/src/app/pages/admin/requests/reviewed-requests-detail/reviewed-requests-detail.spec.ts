import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewedRequestsDetail } from './reviewed-requests-detail';

describe('ReviewedRequestsDetail', () => {
  let component: ReviewedRequestsDetail;
  let fixture: ComponentFixture<ReviewedRequestsDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewedRequestsDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewedRequestsDetail);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
