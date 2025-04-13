import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { RoomService } from '../../services/room.service';
import { RoomRequest } from '../../model/class/Request/RoomRequest';
import { CleaningService } from '../../services/cleaning.service';
import { FormsModule } from '@angular/forms';
import { ToastnotificationService } from '../../services/toastnotification.service';
import { CleaningOnRequest } from '../../model/class/Request/CleaningOnRequest';

@Component({
  selector: 'app-requestcleaning',
  imports: [CommonModule, FormsModule],
  templateUrl: './requestcleaning.component.html',
  styleUrl: './requestcleaning.component.css'
})
export class RequestcleaningComponent implements OnInit{


  rooms: RoomRequest[] = [];

  roomService = inject(RoomService);
  cleaningService = inject(CleaningService);
  toastNotif = inject(ToastnotificationService);
  

  roomId!: number;
  message!: string;


  token!: string | null;

  ngOnInit(): void {
      this.token = localStorage.getItem('jwtToken');
      this.getRoomsWithCustomCleaning();

  }


  getRoomsWithCustomCleaning(): void{
    if(this.token){
      this.roomService.getRoomsWithCustomCleaningType(this.token).subscribe({
        next: (data) => {
          this.rooms = data;
          this.roomId = this.rooms[0].roomId;
        },
        error: (error) => console.error('Error fetching rooms:', error)
      });
    }
    
  }

  sendRequest(): void{
    if(this.token){
    this.cleaningService.requestCleaning(this.token, this.roomId, this.message).subscribe({
      next: (response)=>{
        this.toastNotif.showSuccess(response.message, 'Request Submitted');
        this.message = "";
        this.roomId = this.rooms[0].roomId;
      },
      error: (error) => console.error(error)
    })
    }
  }

  


}
