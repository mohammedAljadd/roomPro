import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { UserbookingsService } from '../../services/userbookings.service';
import { BookingRequest } from '../../model/class/Request/BookingRequest';
import { Router } from '@angular/router';
import { ToastnotificationService } from '../../services/toastnotification.service';

@Component({
  selector: 'app-mybookings',
  imports: [CommonModule],
  templateUrl: './mybookings.component.html',
  styleUrl: './mybookings.component.css'
})
export class MybookingsComponent implements OnInit {
  
  constructor(private cdr: ChangeDetectorRef, private router: Router){};
  
  userBookings: BookingRequest[] = [];

  selectedBookings: BookingRequest[] = []; // for active tabs

  activeTab: string = '';
  
  selectedBooking: BookingRequest | null = null; // to be canceled

  userbookingsService = inject(UserbookingsService);
  toastNotif = inject(ToastnotificationService);

  now!: Date;

  token!: string | null;
  

  ngOnInit(): void {
    // Get the token from localStorage or other places
    this.token = localStorage.getItem('jwtToken');
    this.fetchBookings();
    

  }


  fetchBookings(){
    this.activeTab = 'upcoming';
    

    if (this.token) {
      // Call the service to get user bookings
      this.userbookingsService.getUserBookings(this.token).subscribe({
        next: (data) => {
          this.userBookings = data;  // Assign the fetched bookings to userBookings
          this.selectedBookings = this.userBookings.filter(booking=>{
            const now = new Date();
            const start = new Date(booking.startTime);
            return start > now && booking.canceled !== true;
          });
        },
        error: (error) => {
          console.error('Error fetching user bookings:', error);
        }
      });
    } else {
      this.router.navigate(['/login']);
    }
  }

  getCurrentTime(){
    this.now = new Date();
    return this.now;
  }

  getDateFromString(date: string){
    return new Date(date);
  }

  isUpcoming(booking: BookingRequest){
    return booking.canceled !== true && this.getCurrentTime() < this.getDateFromString(booking.startTime);
  }

  getNoBookingMessage(): string {
    switch (this.activeTab) {
      case 'upcoming':
        return 'You have no upcoming bookings.';
      case 'completed':
        return 'You have no completed bookings.';
      case 'cancelled':
        return 'You have no cancelled bookings.';
      default:
        return 'You have no bookings.';
    }
  }


  selectTab(tab: string){
    this.activeTab = tab;


      const now = new Date();
      
      this.selectedBookings = this.userBookings.filter(booking=>{
        const start = new Date(booking.startTime);
        const end = new Date(booking.endTime);

        if (tab === 'upcoming') {
    
          return start > now && booking.canceled !== true;
        } else if (tab === 'completed') {
    
          return end < now && booking.canceled !== true;
        } else if (tab === 'cancelled') {
          return booking.canceled === true;
        }

        return false;


      })
    
  }

  openBookingCancelationForm(userBooking: BookingRequest): void {
    this.selectedBooking = userBooking;
      const modal = new window.bootstrap.Modal(document.getElementById('cancelBookingModal'));
      modal.show();
    }

  cancelBooking(bookingId: number): void {
    console.log("sdsdsd");
    const token = localStorage.getItem('jwtToken');
    if (token) {
      
      this.userbookingsService.cancelBooking(token, bookingId).subscribe({
        next: response  => {

          
          this.toastNotif.showSuccess(response.message, 'Booking Cancelled');
          this.fetchBookings();
          this.selectedBookings = this.userBookings.filter(booking=>{
            const now = new Date();
            const start = new Date(booking.startTime);
            return start > now && booking.canceled !== true;
          });
          this.selectedBookings = this.selectedBookings.filter(booking => booking.bookingId !== bookingId); // important, we have to make changes to component to be able to use cdr
          this.cdr.detectChanges();
          
        },
        error: error => {
          console.error('Error fetching user bookings:', error);
        }
      });
    } else {
      console.log('No token found');
    }

    document.body.focus();  // This ensures no element inside the modal retains focus

    // Close the modal properly
    const modalElement = document.getElementById('cancelBookingModal');
    if (modalElement) {
      const modal = window.bootstrap.Modal.getInstance(modalElement);
      if (modal) {
        modal.hide();
      }
    }
    } 
}
