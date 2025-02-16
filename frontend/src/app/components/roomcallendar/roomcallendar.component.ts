import { Component, inject, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FullCalendarModule } from '@fullcalendar/angular';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import { BookingService } from '../../services/booking.service';
import { CalendarOptions } from '@fullcalendar/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Booking } from '../../model/class/Booking';

@Component({
  selector: 'app-roomcallendar',
  imports: [FullCalendarModule, CommonModule, FormsModule],
  templateUrl: './roomcallendar.component.html',
  styleUrls: ['./roomcallendar.component.css']
})
export class RoomcallendarComponent implements OnInit {
  

  bookingTime: string = '';  // Time selected for booking
  bookingHours: number = 0;
  userBooking: Booking = {
    roomId: 0, 
    startTime: '',
    bookingHours: 0
  };

  roomId!: number;

  roomBookings: { start: Date; end: Date }[] = [];

  @Input() roomName: string = '';

  calendarOptions: CalendarOptions = {
    initialView: 'timeGridWeek', // Show week view with time slots
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
    slotMinTime: '08:00:00', // Start at 08:00 AM
    slotMaxTime: '18:00:00', // End at 06:00 PM
    hiddenDays: [0, 6], // Hide Sunday (0) and Saturday (6)
    events: [],
    titleFormat: { 
      month: 'long', // Full month name
      year: 'numeric', // Full year
      day: 'numeric' // Day of the month
    },
    expandRows: true,
  
    allDaySlot: false, // Disable the all-day slot
    nowIndicator: true, // Show a now-indicator to indicate the current time
    height: "600px",
  };

  

  bookingService = inject(BookingService);

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.roomId = Number(this.route.snapshot.paramMap.get('id'));  
    this.roomName = String(this.route.snapshot.paramMap.get('roomName'));
    this.fetchBookings();
  }

  fetchBookings(): void {
    const token = localStorage.getItem('jwtToken');
    if (token) {
      this.bookingService.getBookingsByRoomId(token, this.roomId).subscribe({
        next: (data) => {

          // Map the data and log each individual booking's start and end date
          this.roomBookings = data.map(booking => {
            const start = new Date(booking.startTime);
            const end = new Date(new Date(booking.endTime));

            console.log("Start Date: ", start);
            console.log("End Date: ", end);

            return {
              start: start,
              end: end
            };
          });


          this.loadEvents();
        },
        error: (error) => console.error('Error fetching bookings:', error)
      });
    } else {
      console.log("No token found");
    }
  }

  loadEvents(): void {
    this.calendarOptions.events = this.roomBookings.map(booking => ({
      title: 'Booked Slot',
      start: booking.start,
      end: booking.end,
      backgroundColor: '#ff5733',
      borderColor: '#ff5733'
    }));
  }


  onSubmitBooking(): void {

  
    // Send booking details along with token
    this.userBooking.roomId=this.roomId;
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

}
}