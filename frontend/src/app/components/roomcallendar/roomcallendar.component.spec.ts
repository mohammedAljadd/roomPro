import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomcallendarComponent } from './roomcallendar.component';

describe('RoomcallendarComponent', () => {
  let component: RoomcallendarComponent;
  let fixture: ComponentFixture<RoomcallendarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoomcallendarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoomcallendarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
