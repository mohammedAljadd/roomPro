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
import { MaintenanceService } from '../../services/maintenance.service';
import { HolidayService } from '../../services/holiday.service';

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

  maintenanceSlots: { roomId: number, startDate: string, endDate: string}[] = [];

  weeklyCleanings: { roomId: number, starttime: string; endtime: string, cleaningDay: string, setDate: string} = {
    roomId: 0,     
    starttime: '',     
    endtime: '',       
    cleaningDay: '',
    setDate: '',
  };
  
  holidays: {date: string, name: string}[] = [];



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

      else if(arg.event.id.startsWith('maintenance_slot')){
        return { html: `<div class="fc-event-title">Under maintenance</div>` };
      }

      else if(arg.event.id === "holiday_slot"){
        return { html: `<div class="fc-event-title">${arg.event.title}</div>` };
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
  maintenanceService = inject(MaintenanceService);
  holidayService = inject(HolidayService);

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
      this.fetchHolidays(),
      this.fetchAfterUseCleanings(),
      this.fetchWeeklyCleaning(),
      this.fetchMaitenanceSlots()
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

  fetchMaitenanceSlots(): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.token) {
        this.maintenanceService.fetchAMaitenanceSlots(this.token, this.roomId).subscribe({
          next: (data) => {
            this.maintenanceSlots = data;
            resolve();
          },
          error: (error) => {
            console.error('Error fetching maitenance slots:', error);
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

    // get all dates between two dates (inclusive)
    const getDaysBetween = (start: Date, end: Date): Date[] => {
      const result = [];
      const current = new Date(start);
      while (current <= end) {
        result.push(new Date(current));
        current.setDate(current.getDate() + 1);
      }
      return result;
    };

    // Prepare holiday date set (only dates, no time)
    const holidayDateSet = new Set(
      this.holidays.map(h => new Date(h.date).toISOString().split('T')[0])
    );

    // Prepare maintenance date set (exclude holidays from maintenance too)
    const maintenanceDateSet = new Set<string>();
    for (const slot of this.maintenanceSlots) {
      const start = new Date(slot.startDate);
      const end = new Date(slot.endDate);
      const allDates = getDaysBetween(start, end);

      for (const date of allDates) {
        const dateStr = date.toISOString().split('T')[0];
        if (!holidayDateSet.has(dateStr)) {
          maintenanceDateSet.add(dateStr);
        }
      }
    }

    // Combine holidays + maintenance dates to exclude from weekly cleaning
    const excludedDates = new Set([...holidayDateSet, ...maintenanceDateSet]);

    // Prepare exdates array for RRule, including time like "2025-09-15T08:00:00"
    const hourStart = this.weeklyCleanings?.starttime || '08:00';
    const exdates = Array.from(excludedDates).map(date => `${date}T${hourStart}:00`);

    // Holiday events
    const holidayEvents: any[] = this.holidays.map((holiday, i) => {
      const startDate = holiday.date;
      const nextDate = new Date(startDate);
      nextDate.setDate(nextDate.getDate() + 1);
      return {
        title: holiday.name,
        startRecur: startDate,
        endRecur: nextDate.toISOString().split('T')[0],
        daysOfWeek: [new Date(startDate).getDay().toString()],
        startTime: '08:00',
        endTime: '23:59',
        backgroundColor: '#ff1f1f',
        borderColor: '#ff1f1f',
        id: `holiday_slot_${i}`
      };
    });

    // After-use cleaning events
    const cleaningEvents = this.afterUseCleanings.map((cleaning, index) => ({
      title: 'After use Cleaning',
      start: cleaning.start,
      end: cleaning.end,
      backgroundColor: '#02ab2f',
      borderColor: '#02ab2f',
      id: `cleaning_after_use_${index}_${cleaning.start.getTime()}`,
    }));

    // Booking events
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
        backgroundColor: isCurrentUser ? '#4682fa' : '#f505dd',
        borderColor: isCurrentUser ? '#4682fa' : '#f505dd'
      };
    });

    // Maintenance events (split into days)
    const maintenanceEvents: any[] = [];
    for (let i = 0; i < this.maintenanceSlots.length; i++) {
      const slot = this.maintenanceSlots[i];
      const start = new Date(slot.startDate);
      const end = new Date(slot.endDate);
      const allDates = getDaysBetween(start, end);

      for (let j = 0; j < allDates.length; j++) {
        const currentDate = allDates[j];
        const dateStr = currentDate.toISOString().split('T')[0];
        if (holidayDateSet.has(dateStr)) continue; // skip holiday days here

        let startTime = '08:00';
        let endTime = '18:00';

        if (j === 0) startTime = slot.startDate.split('T')[1];
        if (j === allDates.length - 1) endTime = slot.endDate.split('T')[1];

        maintenanceEvents.push({
          title: 'Under maintenance',
          startRecur: dateStr,
          endRecur: new Date(currentDate.getTime() + 24 * 60 * 60 * 1000).toISOString().split('T')[0],
          daysOfWeek: [currentDate.getDay().toString()],
          startTime,
          endTime,
          backgroundColor: '#c7c3c3',
          borderColor: '#c7c3c3',
          id: `maintenance_slot_${slot.roomId}_${j}`
        });
      }
    }

    // Weekly cleaning recurring event with exdates (excluded holidays + maintenance)
    const recurringCleaning: any[] = [];
    if (this.weeklyCleanings != null) {
      const weekdayMap: { [key: string]: any } = {
        monday: RRule.MO,
        tuesday: RRule.TU,
        wednesday: RRule.WE,
        thursday: RRule.TH,
        friday: RRule.FR,
        saturday: RRule.SA,
        sunday: RRule.SU
      };

      const cleaningDay = this.weeklyCleanings.cleaningDay.toLowerCase();
      const weekDay = weekdayMap[cleaningDay];
      const dayStart = this.weeklyCleanings.setDate.split('T')[0];
      const duration = this.getDuration(this.weeklyCleanings.starttime, this.weeklyCleanings.endtime);

      recurringCleaning.push({
        title: 'Weekly cleaning',
        rrule: {
          freq: 'weekly',
          byweekday: [weekDay],
          dtstart: dayStart + 'T' + hourStart,
        },
        exdate: exdates, // exclude holidays and maintenance days here
        id: 'recurring_cleaning',
        duration: duration,
        backgroundColor: '#ff9800',
        borderColor: '#ff9800'
      });
    }

    // Set all events into calendar
    this.calendarOptions.events = [
      ...holidayEvents,
      ...maintenanceEvents,
      ...recurringCleaning,
      ...cleaningEvents,
      ...bookingEvents
    ];
  }


  


  async onSubmitBooking(): Promise<void> {
    this.userBooking.roomId = this.roomId;
    this.userBooking.startTime = this.bookingTime;
    this.userBooking.bookingHours = this.bookingHours;
    
    // Check if booking done in a holiday
    let isBookedOnHoliday = false;
    let holidayName = '';
    for(let i=0; i<this.holidays.length; i++){
      let holidayDate = this.holidays[i].date;
      holidayName = this.holidays[i].name;
      let bookingDate = this.userBooking.startTime.split('T')[0];
      if(holidayDate==bookingDate){
        isBookedOnHoliday = true;
        break;
      }
    }

    if(isBookedOnHoliday){
      this.toastNotif.showError(`Booking can't be scheduled on a holiday day: ${holidayName}`, "Booking failed");
    }
    else{

      this.bookingService.submitBooking(this.userBooking).subscribe(
        response => {
          // Refresh all data after successful booking
          Promise.all([
            this.fetchBookings(),
            this.fetchHolidays(),
            this.fetchAfterUseCleanings(),
            this.fetchWeeklyCleaning(),
            this.fetchMaitenanceSlots()
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
    
  }


  fetchHolidays(): Promise<void> {
    const currentYear = new Date().getFullYear();    
    let country = "FR";
    
    return new Promise((resolve, reject) => {
      this.holidayService.fetHolidays(currentYear.toString(), country).subscribe({
        next: (data) => {
          this.holidays = data.map((holiday: any) => ({
            date: holiday.date,
            name: holiday.localName
          }));
          resolve();
        },
        error: (error) => {
          console.error('Error fetching holidays:', error);
          reject(error);
        }
      });
    });
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