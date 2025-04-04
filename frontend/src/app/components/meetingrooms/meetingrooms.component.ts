import { Component, inject, OnInit } from '@angular/core';
import { RoomService } from '../../services/room.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { BookingService } from '../../services/booking.service';
import { Router } from '@angular/router';
import { RoomRequest } from '../../model/class/Request/RoomRequest';
import { BookingResponse } from '../../model/class/Response/BookingResponse';

declare global {
  interface Window {
    bootstrap: any;  // Declare bootstrap property on the window object
  }
}

@Component({
  selector: 'app-meetingrooms',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './meetingrooms.component.html',
  styleUrls: ['./meetingrooms.component.css']
})
export class MeetingroomsComponent implements OnInit {
  rooms: RoomRequest[] = [];
  filteredRooms: RoomRequest[] = [];
  selectedCapacity: number = 0;
  selectedLocation: string = '';
  selectedEquipment: string[] = [];
  ChosenRoomName!: string 

  uniqueLocations: string[] = [];
  uniqueEquipment: string[] = [];
  
  selectedRoom: RoomRequest | null = null;  // To store selected room for booking
  bookingTime: string = '';  // Time selected for booking
  bookingHours: number = 0;
  token = localStorage.getItem('jwtToken');
  userBooking: BookingResponse = {
    roomId: 0, 
    startTime: '',
    bookingHours: 0
  };

  bookingService = inject(BookingService);

  constructor(private roomService: RoomService, private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.roomService.getAllRooms().subscribe({
      next: (data) => {
        this.rooms = data;
        
        // Sort rooms based on location
        this.rooms.sort((a, b) => {
          const order = ['Main', 'East', 'West'];
          const locationComparison =  order.indexOf(a.location.split(' ')[0]) - order.indexOf(b.location.split(' ')[0]);
          if(locationComparison==0){
            return a.name.localeCompare(b.name);
          }
          return locationComparison;
        });


        this.filteredRooms = data;
        this.uniqueLocations = [...new Set(data.map(room => room.location))];
        this.uniqueEquipment = [...new Set(data.flatMap(room => room.equipments))];
      },
      error: (error) => console.error('Error fetching rooms:', error)
    });

  }

  applyFilters(): void {
    this.getFilteredRooms(); 
  }

  getFilteredRooms(): void {
    const equipmentFilter = this.selectedEquipment.join(',');
  
    this.roomService.getFilteredRooms(this.selectedCapacity, this.selectedLocation, equipmentFilter).subscribe({
      next: (data) => {
        this.filteredRooms = data; // store filtered rooms in filteredRooms array
      },
      error: (error) => console.error('Error fetching filtered rooms:', error)
    });
  }


  




  
  openRoomCalendarPage(room: RoomRequest):void{
    this.ChosenRoomName = room.name;
    this.router.navigate(['/meeting-rooms', room.roomId, room.name]);
  }

}