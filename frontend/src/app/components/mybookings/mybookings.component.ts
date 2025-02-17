import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { UserbookingsService } from '../../services/userbookings.service';
import { BookingRequest } from '../../model/class/Request/BookingRequest';
import { Router } from '@angular/router';

@Component({
  selector: 'app-mybookings',
  imports: [CommonModule],
  templateUrl: './mybookings.component.html',
  styleUrl: './mybookings.component.css'
})
export class MybookingsComponent implements OnInit {
  
  constructor(private cdr: ChangeDetectorRef, private router: Router){};
  
  userBookings: BookingRequest[] = [];
  
  selectedBooking: BookingRequest | null = null;

  userbookingsService = inject(UserbookingsService);

  

  ngOnInit(): void {
    // Get the token from localStorage or other places
    const token = localStorage.getItem('jwtToken');

    if (token) {
      // Call the service to get user bookings
      this.userbookingsService.getUserBookings(token).subscribe({
        next: (data) => {
          this.userBookings = data;  // Assign the fetched bookings to userBookings
        },
        error: (error) => {
          console.error('Error fetching user bookings:', error);
        }
      });
    } else {
      this.router.navigate(['/login']);
    }

  }

  openBookingCancelationForm(userBooking: BookingRequest): void {
    this.selectedBooking = userBooking;
      const modal = new window.bootstrap.Modal(document.getElementById('cancelBookingModal'));
      modal.show();
    }

  cancelBooking(bookingId: number): void {
    const token = localStorage.getItem('jwtToken');
    if (token) {
      this.userbookingsService.cancelBooking(token, bookingId).subscribe({
        next: response  => {
          this.userBookings = this.userBookings.filter(booking => booking.bookingId !== bookingId); // important, we have to make changes to component to be able to use cdr
          this.cdr.detectChanges();

        },
        error: error => {
          console.error('Error fetching user bookings:', error);
        }
      });
    } else {
      console.log('No token found');
    }
  }
}
