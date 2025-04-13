import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserrequestedcleaningComponent } from './userrequestedcleaning.component';

describe('UserrequestedcleaningComponent', () => {
  let component: UserrequestedcleaningComponent;
  let fixture: ComponentFixture<UserrequestedcleaningComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserrequestedcleaningComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserrequestedcleaningComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
