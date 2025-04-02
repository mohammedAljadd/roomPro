import { ChangeDetectorRef, Component, ElementRef, inject, Input, OnInit, Renderer2 } from '@angular/core';
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
import { RRule } from 'rrule';
import rrulePlugin from '@fullcalendar/rrule';

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

  token!: string | null;

  isAdmin: boolean = false;

  roomId!: number;

  roomBookings: { start: Date; end: Date ; userEmail: string}[] = [];

  afterUseCleanings: { roomId: number, start: Date; end: Date}[] = [];
  weeklyCleanings: { roomId: number, starttime: string; endtime: string, cleaningDay: string, setDate: string} = {
    roomId: 0,     
    starttime: '',     
    endtime: '',       
    cleaningDay: '',
    setDate: '',
  };
  

  @Input() roomName: string = '';

  calendarOptions: CalendarOptions = {
    initialView: 'timeGridWeek', // Show week view with time slots
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin, rrulePlugin],
    slotMinTime: '08:00:00', // Start at 08:00 AM
    slotMaxTime: this.isAdmin ? '18:00:00' : '20:00:00', // End at 06:00 PM
    hiddenDays: this.isAdmin ? [0, 6] : [], // Hide Sunday (0) and Saturday (6)
    firstDay: 1,
    contentHeight : 700,

    events: [],
    
    eventContent: (arg) => {
      // For cleaning events, only show the title
      
      if (arg.event.id.startsWith('cleaning_after_use')) {
        return { html: `<div class="fc-event-title">Cleaning - after usage</div>` };
      }

      else if(arg.event.id === 'recurring_cleaning'){
        return { html: `<div class="fc-event-title">Cleaning - weekly</div>` };
      
      }
      
      // For other events, show the default content
      return { html: `<div class="fc-event-time">${arg.timeText}</div><div class="fc-event-title fc-sticky">${arg.event.title}</div>` };
    }, 
    


    titleFormat: { 
      month: 'long', // Full month name
      year: 'numeric', // Full year
      day: 'numeric' // Day of the month
    },

    eventDisplay: 'block',
    eventTimeFormat: { // Optional: format time display
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    },
    
    
    slotMinWidth: 40,
    expandRows: true, // Already set, but keep this
  
    allDaySlot: false, // Disable the all-day slot
    nowIndicator: true, // Show a now-indicator to indicate the current time
  };

  bookingService = inject(BookingService);
  cleaningService = inject(CleaningService);

  toastNotif = inject(ToastnotificationService);

  constructor(private route: ActivatedRoute, private cdr: ChangeDetectorRef) {}
  
  async ngOnInit(): Promise<void> {
    this.token = localStorage.getItem('jwtToken');
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset()); // Adjust for timezone
    this.minDateTime = now.toISOString().slice(0, 16);
    
    this.roomId = Number(this.route.snapshot.paramMap.get('id'));  
    this.roomName = String(this.route.snapshot.paramMap.get('roomName'));
    await Promise.all([
      this.fetchBookings(),
      this.fetchAfterUseCleanings(),
      this.fetchWeeklyCleaning()
    ]);

    this.loadEvents();
    
    
    if (this.token) {
      this.isAdmin = JSON.parse(atob(this.token.split('.')[1])).role == "Admin";
    }

  }

  fetchAfterUseCleanings(): Promise<void> {
    return new Promise((resolve, reject) => {
      if(this.token){
        this.cleaningService.fetchAfterUseCleanings(this.token, this.roomId).subscribe({
          next: (data)=>{
            this.afterUseCleanings = data.map(cleaning => ({
              roomId: cleaning.roomId,
              start: new Date(cleaning.startTime),
              end: new Date(cleaning.endTime)
            }));
            resolve();
          },
          error: (error)=>{
            console.log("Error");
            reject(error);
          }
        });
      } else {
        resolve();
      }
    });
  }

  fetchWeeklyCleaning(): Promise<void> {
    return new Promise((resolve, reject) => {
      if(this.token){
        this.cleaningService.fetchWeeklyCleaning(this.token, this.roomId).subscribe({
          next: (data)=>{
            this.weeklyCleanings = data;
            resolve();
          },
          error: (error)=>{
            console.log("Error");
            reject(error);
          }
        });
      } else {
        resolve();
      }
    });
  }


  fetchBookings(): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.token) {
        this.bookingService.getBookingsByRoomId(this.token, this.roomId).subscribe({
          next: (data) => {
            this.roomBookings = data.map(booking => ({
              start: new Date(booking.startTime),
              end: new Date(booking.endTime),
              userEmail: booking.userEmail
            }));
            resolve();
          },
          error: (error) => {
            console.error('Error fetching bookings:', error);
            reject(error);
          }
        });
      } else {
        resolve();
      }
    });
  }
  
  
  getDuration(start: string, end: string): string {
    const [startHour, startMin] = start.split(':').map(Number);
    const [endHour, endMin] = end.split(':').map(Number);
  
    const startTotal = startHour * 60 + startMin;
    const endTotal = endHour * 60 + endMin;
  
    const diff = endTotal - startTotal;
    const hours = Math.floor(diff / 60);
    const minutes = diff % 60;
  
    return `${hours}:${minutes < 10 ? '0' + minutes : minutes}`;
  }


  loadEvents(): void {
    const userEmail = this.getUserEmail();
  
    // Create cleaning events
    const cleaningEvents = this.afterUseCleanings.map((cleaning, index) => ({
      title: 'After use Cleaning',
      start: cleaning.start,
      end: cleaning.end,
      backgroundColor: '#02ab2f',
      borderColor: '#02ab2f',
      id: `cleaning_after_use_${index}_${cleaning.start.getTime()}`,
    }));


  
    // Create booking events
    const bookingEvents = this.roomBookings.map((booking, index) => {
      const isCurrentUser = userEmail === booking.userEmail;
      const startTime = new Date(booking.start);
      const endTime = new Date(booking.end);
      const timeDifference = endTime.getTime() - startTime.getTime();
      const durationInHours = Math.floor(timeDifference / (1000 * 3600));
      const durationInMinutes = Math.floor((timeDifference % (1000 * 3600)) / (1000 * 60));
      const roundedDuration = `${durationInHours} hour${durationInHours !== 1 ? 's' : ''} ${durationInMinutes} minute${durationInMinutes !== 1 ? 's' : ''}`;
  
      return {
        title: `${roundedDuration}`,
        id: `booking_${index}_${startTime.getTime()}`,
        start: booking.start,
        end: booking.end,
        email: booking.userEmail,
        backgroundColor: isCurrentUser ? '#4682fa' : '#f50505',
        borderColor: isCurrentUser ? '#4682fa' : '#f50505'
      };
    });

    // Recurring
    if(this.weeklyCleanings != null){
      const weekdayMap: { [key: string]: any } = {
        monday: RRule.MO,
        tuesday: RRule.TU,
        wednesday: RRule.WE,
        thursday: RRule.TH,
        friday: RRule.FR,
        saturday: RRule.SA,
        sunday: RRule.SU
      };
  
      let cleaningDay = this.weeklyCleanings.cleaningDay.toLocaleLowerCase();
      let weekDay = weekdayMap[cleaningDay];
      let dayStart = this.weeklyCleanings.setDate.split('T')[0];
      let hourStart = this.weeklyCleanings.starttime;
      let duration = this.getDuration(this.weeklyCleanings.starttime, this.weeklyCleanings.endtime);
  
      const recurringEvent = {
        title: 'Weekly cleaning',
        rrule: {
          freq: 'weekly',
          byweekday: [weekDay],
          dtstart: dayStart+'T'+hourStart,
        },
        id: 'recurring_cleaning',
        duration: duration,
        backgroundColor: '#ff9800', // Orange color
        borderColor: '#ff9800'
      };
      // Merge both cleaning and booking events
      this.calendarOptions.events = [recurringEvent, ...cleaningEvents, ...bookingEvents];
  
    }
    else{
      this.calendarOptions.events = [...cleaningEvents, ...bookingEvents];
    }
  
    
    // Force Angular to detect changes
    this.cdr.detectChanges();
  }
  


  async onSubmitBooking(): Promise<void> {
    this.userBooking.roomId = this.roomId;
    this.userBooking.startTime = this.bookingTime;
    this.userBooking.bookingHours = this.bookingHours;
    
    this.bookingService.submitBooking(this.userBooking).subscribe(
      response => {
        // Refresh all data after successful booking
        Promise.all([
          this.fetchBookings(),
          this.fetchAfterUseCleanings(),
          this.fetchWeeklyCleaning()
        ]).then(() => this.loadEvents());
    
        this.toastNotif.showSuccess('Your booking was successful! Thank you for using our service.', 'Booking Confirmed');
      },
      error => {
        console.error("Booking error:", error); // Log full error for debugging
    
        const errorMessage = error?.error || 'An unexpected error occurred. Please try again.';
        this.toastNotif.showError(errorMessage, 'Booking Failed');
      }
    );
    
  }

 getUserEmail(): string | null {
  
  if (!this.token) return null;

  try {
    const payload = JSON.parse(atob(this.token.split('.')[1])); // Decode and parse payload
    return payload?.sub || null;
  } catch (e) {
    console.error('Invalid token:', e);
    return null;
  }
}



}