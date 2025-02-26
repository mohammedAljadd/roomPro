import { Component, inject, OnInit } from '@angular/core';
import { RoomRequest } from '../../model/class/Request/RoomRequest';
import { RoomService } from '../../services/room.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EquipementService } from '../../services/equipement.service';
import { EquipementRequest } from '../../model/class/Request/EquipementRequest';

@Component({
  selector: 'app-managerooms',
  imports: [CommonModule, FormsModule],
  templateUrl: './managerooms.component.html',
  styleUrl: './managerooms.component.css'
})
export class ManageroomsComponent implements OnInit{

  rooms: RoomRequest[] = [];
  filteredRooms: RoomRequest[] = [];
  chosenRoomName: string = "";
  allEquipments: EquipementRequest[] = [];

  roomService = inject(RoomService);
  equipmentService = inject(EquipementService);

  ngOnInit(): void {
    this.roomService.getAllRooms().subscribe({
      next: (data) => {
        this.rooms = data;
        this.filteredRooms = data;
      },
      error: (error) => console.error('Error fetching rooms:', error)
    });

  }

  getChosenRoomName(){
    this.filterRoomByName(this.chosenRoomName);
    
  }

  filterRoomByName(roomeName: string){
    this.filteredRooms = this.rooms.filter(room => room.name.toLowerCase().startsWith(roomeName.toLowerCase()));

  }


  openEquipemetForm(roomId: number): void {
    const modal = new window.bootstrap.Modal(document.getElementById('equipmentModal'));
    modal.show();

    this.fetchAllEquipements(roomId);
  }


  updateEquipements(){

  }

  fetchAllEquipements(roomId: number){
    const token = localStorage.getItem('jwtToken');

    if (token) {
    this.equipmentService.fetAllEquipements(token, roomId).subscribe({
      next: (data) => {
        this.allEquipments = data;
        console.log("Equipment : " + this.allEquipments[5]);
      },
      error: (error) => console.error('Error fetching rooms:', error)
    });
    }
  }
}
