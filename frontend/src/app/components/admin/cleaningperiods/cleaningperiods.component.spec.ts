import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CleaningperiodsComponent } from './cleaningperiods.component';

describe('CleaningperiodsComponent', () => {
  let component: CleaningperiodsComponent;
  let fixture: ComponentFixture<CleaningperiodsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CleaningperiodsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CleaningperiodsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
