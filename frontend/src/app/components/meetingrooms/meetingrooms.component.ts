import { Component, inject, OnInit } from '@angular/core';
import { Room } from '../../model/class/Room';
import { RoomService } from '../../services/room.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Booking } from '../../model/class/Booking';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BookingService } from '../../services/booking.service';

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
  rooms: Room[] = [];
  filteredRooms: Room[] = [];
  selectedCapacity: number = 0;
  selectedLocation: string = '';
  selectedEquipment: string[] = [];
  
  uniqueLocations: string[] = [];
  uniqueEquipment: string[] = [];
  
  selectedRoom: Room | null = null;  // To store selected room for booking
  bookingTime: string = '';  // Time selected for booking
  bookingHours: number = 0;
  token = localStorage.getItem('jwtToken');
  userBooking: Booking = {
    roomId: 0, 
    startTime: '',
    bookingHours: 0
  };

  bookingService = inject(BookingService);

  constructor(private roomService: RoomService, private http: HttpClient) {}

  ngOnInit(): void {
    this.roomService.getAllRooms().subscribe({
      next: (data) => {
        this.rooms = data;
        this.filteredRooms = data;
        this.uniqueLocations = [...new Set(data.map(room => room.location))];
        this.uniqueEquipment = [
          ...new Set(data.flatMap(room =>
            room.roomEquipmentMappings.flatMap(mapping =>
              mapping.equipment.name
            )
          ))
        ];
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


  
  openBookingForm(room: Room): void {
    this.selectedRoom = room;  // Set the selected room to the clicked room
    // Open the modal using Bootstrap's modal API
    const modal = new window.bootstrap.Modal(document.getElementById('bookingModal'));
    modal.show();
  }



  
  onSubmitBooking(): void {

  
  // Send booking details along with token
  this.userBooking.roomId=this.selectedRoom!.roomId;
  this.userBooking.startTime=this.bookingTime;
  this.userBooking.bookingHours = this.bookingHours;
  
  
  this.bookingService.submitBooking(this.userBooking).subscribe(
    response => {
      console.log('User booked a room successfully:', response.message);
    },
    error => {
      console.error('Booking failed:', error.error?.error || 'Unknown error occurred');
    }
  );

 
  // Move focus away from the modal before closing
  document.body.focus();  // This ensures no element inside the modal retains focus

  // Close the modal properly
  const modalElement = document.getElementById('bookingModal');
  if (modalElement) {
    const modal = window.bootstrap.Modal.getInstance(modalElement);
    if (modal) {
      modal.hide();
    }
  }
  }}