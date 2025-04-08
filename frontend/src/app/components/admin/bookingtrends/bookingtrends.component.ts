import { Component, inject, OnInit } from '@angular/core';
import { BookingTrendsRequest } from '../../../model/class/Request/BookingTrendsRequest';
import { BookingService } from '../../../services/booking.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-bookingtrends',
  imports: [CommonModule],
  templateUrl: './bookingtrends.component.html',
  styleUrl: './bookingtrends.component.css'
})
export class BookingtrendsComponent implements OnInit{

  token!: string | null;

  bookingTrends!: BookingTrendsRequest;

  bookingService = inject(BookingService);
  months: string[] = [
    'January',
    'February',
    'March',
    'April',
    'May',
    'June',
    'July',
    'August',
    'September',
    'October',
    'November',
    'December'
  ];
  
  currentMonthName!: string;
  currentYear!: number;

  ngOnInit(): void {
      this.token = localStorage.getItem('jwtToken');
      this.getBookingTrends();
  }

  getBookingTrends(): void{
    if(this.token != null){
      const today = new Date();
      const currentYear = today.getFullYear();   
      this.currentYear = currentYear; 
      const currentMonth = today.getMonth() + 1;

      this.currentMonthName = this.months[currentMonth-1];

      this.bookingService.getBookingTrends(this.token, currentYear, currentMonth).subscribe({
        next: (data)=>{
          this.bookingTrends = data;
          console.log(this.bookingTrends)
        },
        error: error=>{
          console.log(error.error);
        }
      })
    }
    else{
      console.log("Error");
    }
  }

  getDurationFormat(duration: number): string {
    const hours = Math.floor(duration);
    const minutes = Math.round((duration - hours) * 60);
    return `${hours}h ${minutes}min`;
  }
  



}
