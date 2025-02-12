import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { UserBooking } from '../../model/class/UserBooking';
import { UserbookingsService } from '../../services/userbookings.service';

@Component({
  selector: 'app-mybookings',
  imports: [CommonModule],
  templateUrl: './mybookings.component.html',
  styleUrl: './mybookings.component.css'
})
export class MybookingsComponent implements OnInit {
  
  userBookings: UserBooking[] = [];

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
      console.log('No token found');
    }

  }


  cancelBooking(bookingId: number): void {
   
  }
}
