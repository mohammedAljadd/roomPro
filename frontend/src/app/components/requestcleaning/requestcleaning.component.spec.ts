import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestcleaningComponent } from './requestcleaning.component';

describe('RequestcleaningComponent', () => {
  let component: RequestcleaningComponent;
  let fixture: ComponentFixture<RequestcleaningComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestcleaningComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RequestcleaningComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
