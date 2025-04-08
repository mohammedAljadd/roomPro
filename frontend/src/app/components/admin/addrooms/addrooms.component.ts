import { Component, inject, OnInit } from '@angular/core';
import { NewRoomResponse } from '../../../model/class/Response/NewRoomResponse';
import { FormsModule } from '@angular/forms';
import { RoomService } from '../../../services/room.service';
import { ToastnotificationService } from '../../../services/toastnotification.service';
import { CommonModule } from '@angular/common';
import { EquipementRequest } from '../../../model/class/Request/EquipementRequest';
import { EquipementService } from '../../../services/equipement.service';

@Component({
  selector: 'app-addrooms',
  imports: [FormsModule, CommonModule],
  templateUrl: './addrooms.component.html',
  styleUrl: './addrooms.component.css'
})
export class AddroomsComponent implements OnInit{
  
  newRoom: NewRoomResponse = new NewRoomResponse();
  allEquipments: EquipementRequest[] = [];

  roomService = inject(RoomService);
  toastNotif = inject(ToastnotificationService);
  equipmentService = inject(EquipementService);

  locations: string[] = [];

  ngOnInit(): void {
    this.fetchAllEquipements();
    this.locations = ["Main Building", "West Wing", "East Wing"];
    this.newRoom.location = this.locations[0];
  }

  addEquipment(EquipmentId: number, event: any):void{
    
    if(event.target.checked){
      this.newRoom.equipmentsIDs.push(EquipmentId);
    }
    else{
      this.newRoom.equipmentsIDs = this.newRoom.equipmentsIDs.filter(number => number!=EquipmentId);
    }
  }


  addNewRoom(): void{
    const token = localStorage.getItem('jwtToken');
    if(token!=null){
      this.roomService.addNewRoom(this.newRoom, token).subscribe(
        response => {
         this.toastNotif.showSuccess(response.message);
         this.newRoom = new NewRoomResponse();
         this.newRoom.location = this.locations[0];
         this.newRoom.equipmentsIDs = [];
        },
        error => {
          this.toastNotif.showWarning(error.error);
        }
      );
  
    }
  }

  fetchAllEquipements(){
    const token = localStorage.getItem('jwtToken');

    if (token) {
    this.equipmentService.fetAllEquipements(token, 1).subscribe({
      next: (data) => {
        this.allEquipments = data;
      },
      error: (error) => console.error('Error fetching rooms:', error)
    });
    }
  }

}
