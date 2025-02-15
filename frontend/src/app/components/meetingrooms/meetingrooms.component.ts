import { Component, inject, OnInit } from '@angular/core';
import { Room } from '../../model/class/Room';
import { RoomService } from '../../services/room.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Booking } from '../../model/class/Booking';
import { HttpClient } from '@angular/common/http';
import { BookingService } from '../../services/booking.service';
import { CalendarModule } from 'angular-calendar';
import { CalendarComponent } from '../calendar/calendar.component';
import { addHours } from 'date-fns';

declare global {
  interface Window {
    bootstrap: any;
  }
}

@Component({
  selector: 'app-meetingrooms',
  standalone: true,
  imports: [CommonModule, FormsModule, CalendarModule, CalendarComponent],
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
  
  selectedRoom: Room | null = null;
  bookingTime: string = '';
  bookingHours: number = 0;
  token = localStorage.getItem('jwtToken');

  userBooking: Booking = {
    roomId: 0, 
    startTime: '',
    bookingHours: 0
  };

  bookings: { start: Date; end: Date }[] = []; // Store booking times

  bookingService = inject(BookingService);

  constructor(private roomService: RoomService, private http: HttpClient) {}

  ngOnInit(): void {
    this.loadRooms();
    this.loadBookings(); // Load existing bookings when the component initializes
  }

  loadRooms(): void {
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

  loadBookings(): void {
    this.bookingService.getAllBookings().subscribe({
      next: (data) => {
        this.bookings = data.map(booking => {
          const startTime = new Date(booking.startTime);
          const endTime = new Date(booking.endTime);
          return { start: startTime, end: endTime };
        });
      },
      error: (error) => console.error('Error fetching bookings:', error)
    });
  }
  
  

  applyFilters(): void {
    this.getFilteredRooms();
  }

  getFilteredRooms(): void {
    const equipmentFilter = this.selectedEquipment.join(',');

    this.roomService.getFilteredRooms(this.selectedCapacity, this.selectedLocation, equipmentFilter).subscribe({
      next: (data) => {
        this.filteredRooms = data;
      },
      error: (error) => console.error('Error fetching filtered rooms:', error)
    });
  }

  openBookingForm(room: Room): void {
    this.selectedRoom = room;
    const modal = new window.bootstrap.Modal(document.getElementById('bookingModal'));
    modal.show();
  }

  onSubmitBooking(): void {
    this.userBooking.roomId = this.selectedRoom!.roomId;
    this.userBooking.startTime = this.bookingTime;
    this.userBooking.bookingHours = this.bookingHours;

    this.bookingService.submitBooking(this.userBooking).subscribe(
      response => {
        console.log('User booked a room successfully:', response.message);
        this.loadBookings(); // Refresh bookings after a successful submission
      },
      error => {
        console.error('Booking failed:', error.error?.error || 'Unknown error occurred');
      }
    );

    document.body.focus();
    const modalElement = document.getElementById('bookingModal');
    if (modalElement) {
      const modal = window.bootstrap.Modal.getInstance(modalElement);
      if (modal) {
        modal.hide();
      }
    }
  }
}
