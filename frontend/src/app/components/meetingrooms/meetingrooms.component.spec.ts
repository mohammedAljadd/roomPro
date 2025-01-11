import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MeetingroomsComponent } from './meetingrooms.component';

describe('MeetingroomsComponent', () => {
  let component: MeetingroomsComponent;
  let fixture: ComponentFixture<MeetingroomsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MeetingroomsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MeetingroomsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
