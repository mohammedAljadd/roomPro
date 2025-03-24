import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookingtrendsComponent } from './bookingtrends.component';

describe('BookingtrendsComponent', () => {
  let component: BookingtrendsComponent;
  let fixture: ComponentFixture<BookingtrendsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookingtrendsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookingtrendsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
