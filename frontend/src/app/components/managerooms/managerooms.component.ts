import { Component, inject, OnInit } from '@angular/core';
import { RoomRequest } from '../../model/class/Request/RoomRequest';
import { RoomService } from '../../services/room.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

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

  roomService = inject(RoomService);

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


}
