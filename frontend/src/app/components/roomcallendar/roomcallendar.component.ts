import { ChangeDetectorRef, Component, inject, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FullCalendarModule } from '@fullcalendar/angular';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import { BookingService } from '../../services/booking.service';
import { CalendarOptions } from '@fullcalendar/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingResponse } from '../../model/class/Response/BookingResponse';
import { ToastnotificationService } from '../../services/toastnotification.service';
import { CleaningService } from '../../services/cleaning.service';

@Component({
  selector: 'app-roomcallendar',
  imports: [FullCalendarModule, CommonModule, FormsModule],
  templateUrl: './roomcallendar.component.html',
  styleUrls: ['./roomcallendar.component.css']
})
export class RoomcallendarComponent implements OnInit {
  


  bookingTime: string = '';  // Time selected for booking
  bookingHours: number = 0;
  userBooking: BookingResponse = {
    roomId: 0, 
    startTime: '',
    bookingHours: 0
  };

  minDateTime!: string;

  isAdmin: boolean = false;

  roomId!: number;

  roomBookings: { start: Date; end: Date ; userEmail: string}[] = [];

  afterUseCleanings: { roomId: number, start: Date; end: Date}[] = [];

  @Input() roomName: string = '';

  calendarOptions: CalendarOptions = {
    initialView: 'timeGridWeek', // Show week view with time slots
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
    slotMinTime: '08:00:00', // Start at 08:00 AM
    slotMaxTime: this.isAdmin ? '18:00:00' : '20:00:00', // End at 06:00 PM
    hiddenDays: this.isAdmin ? [0, 6] : [], // Hide Sunday (0) and Saturday (6)
    firstDay: 1,
   

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
  cleaningService = inject(CleaningService);

  toastNotif = inject(ToastnotificationService);

  constructor(private route: ActivatedRoute, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset()); // Adjust for timezone
    this.minDateTime = now.toISOString().slice(0, 16);
    
    this.roomId = Number(this.route.snapshot.paramMap.get('id'));  
    this.roomName = String(this.route.snapshot.paramMap.get('roomName'));
    this.fetchBookings();
    this.fetchAfterUseCleanings();

    const token = localStorage.getItem('jwtToken');
    if (token) {
      this.isAdmin = JSON.parse(atob(token.split('.')[1])).role == "Admin";
    }
  }

  fetchAfterUseCleanings(): void{
    const token = localStorage.getItem('jwtToken');
    if(token){
      this.cleaningService.fetchAfterUseCleanings(token, this.roomId).subscribe({
        next: (data)=>{
          this.afterUseCleanings = data.map(cleaning=>{
            const start = new Date(cleaning.startTime);
            const end = new Date(new Date(cleaning.endTime));
            return {
              roomId: cleaning.roomId,
              start: start,
              end: end,
            }
          })
          
        },
        error: (error)=>{
          console.log("Error");
        }
      })
    }
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
            const userEmail = booking.userEmail;

            return {
              start: start,
              end: end,
              userEmail: userEmail
            };
          });


          this.loadEvents();
        },
        error: (error) => console.error('Error fetching bookings:', error)
      });
    } else {
      this.toastNotif.showWarning('You need to be logged in to make a booking. Please log in and try again.', 'Login Required');

    }
  }

  loadEvents(): void {
    const userEmail = this.getUserEmail();
  
    // Create cleaning events
    const cleaningEvents = this.afterUseCleanings.map(cleaning => ({
      title: 'Cleaning',
      start: cleaning.start,
      end: cleaning.end,
      backgroundColor: '#02ab2f',
      borderColor: '#02ab2f',
    }));
  
    // Create booking events
    const bookingEvents = this.roomBookings.map(booking => {
      const isCurrentUser = userEmail === booking.userEmail;
      const startTime = new Date(booking.start);
      const endTime = new Date(booking.end);
      const timeDifference = endTime.getTime() - startTime.getTime();
      const durationInHours = Math.floor(timeDifference / (1000 * 3600));
      const durationInMinutes = Math.floor((timeDifference % (1000 * 3600)) / (1000 * 60));
      const roundedDuration = `${durationInHours} hour${durationInHours !== 1 ? 's' : ''} ${durationInMinutes} minute${durationInMinutes !== 1 ? 's' : ''}`;
  
      return {
        title: `${roundedDuration}`,
        start: booking.start,
        end: booking.end,
        email: booking.userEmail,
        backgroundColor: isCurrentUser ? '#4682fa' : '#f50505',
        borderColor: isCurrentUser ? '#4682fa' : '#f50505'
      };
    });
  
    // Merge both cleaning and booking events
    this.calendarOptions.events = [...cleaningEvents, ...bookingEvents];
  
    // Force Angular to detect changes
    this.cdr.detectChanges();
  }
  


  onSubmitBooking(): void {

  
    // Send booking details along with token
    this.userBooking.roomId=this.roomId;
    this.userBooking.startTime=this.bookingTime;
    this.userBooking.bookingHours = this.bookingHours;
    
    
    this.bookingService.submitBooking(this.userBooking).subscribe(
      response => {
        this.fetchBookings();
        this.cdr.detectChanges();
        this.toastNotif.showSuccess('Your booking was successful! Thank you for using our service.', 'Booking Confirmed');

      },
      error => {
        this.toastNotif.showError(error.error, 'Booking Failed');
      }
    );

    
}

 getUserEmail(): string | null {
  const token = localStorage.getItem('jwtToken');
  if (!token) return null;

  try {
    const payload = JSON.parse(atob(token.split('.')[1])); // Decode and parse payload
    return payload?.sub || null;
  } catch (e) {
    console.error('Invalid token:', e);
    return null;
  }
}



}