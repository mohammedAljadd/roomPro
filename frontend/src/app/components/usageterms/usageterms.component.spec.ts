import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsagetermsComponent } from './usageterms.component';

describe('UsagetermsComponent', () => {
  let component: UsagetermsComponent;
  let fixture: ComponentFixture<UsagetermsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UsagetermsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UsagetermsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
