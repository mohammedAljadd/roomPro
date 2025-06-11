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
  isLoadingLocations = false;

  ngOnInit(): void {
    this.fetchAllEquipements();
    this.fetchLocationsFromDb();
  }

  private getLocationPriority(location: string): number {
    const firstWord = location.split(' ')[0];
    const order = ['Main', 'North', 'South', 'East', 'West'];
    const index = order.indexOf(firstWord);
    return index === -1 ? Number.MAX_SAFE_INTEGER : index;
  }

  fetchLocationsFromDb(): void {
    this.isLoadingLocations = true;
    this.roomService.getAllRooms().subscribe({
      next: (rooms) => {
        this.locations = [...new Set(rooms.map(room => room.location).filter(Boolean))];
        this.locations.sort((a, b) => {
          const byPriority = this.getLocationPriority(a) - this.getLocationPriority(b);
          if (byPriority !== 0) return byPriority;
          return a.localeCompare(b);
        });

        if (!this.newRoom.location || !this.locations.includes(this.newRoom.location)) {
          this.newRoom.location = this.locations[0] ?? '';
        }
        this.isLoadingLocations = false;
      },
      error: (error) => {
        this.isLoadingLocations = false;
        this.locations = [];
        this.newRoom.location = '';
        console.error('Error fetching room locations:', error);
      }
    });
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
    this.newRoom.location = (this.newRoom.location || '').trim();

    if (!this.newRoom.location) {
      this.toastNotif.showWarning('Location is required.');
      return;
    }

    const token = localStorage.getItem('jwtToken');
    if(token!=null){
      this.roomService.addNewRoom(this.newRoom, token).subscribe(
        response => {
         this.toastNotif.showSuccess(response.message);
         this.newRoom = new NewRoomResponse();
         this.newRoom.location = this.locations[0] ?? '';
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
