import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddRemoveEquipmentComponent } from './addremoveequipment.component';

describe('AddRemoveEquipmentComponent', () => {
  let component: AddRemoveEquipmentComponent;
  let fixture: ComponentFixture<AddRemoveEquipmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddRemoveEquipmentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddRemoveEquipmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
