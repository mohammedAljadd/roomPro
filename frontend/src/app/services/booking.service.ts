import { Injectable } from '@angular/core';
import { BookingResponse } from '../model/class/Response/BookingResponse';
import { Observable, throwError } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BookingRequest } from '../model/class/Request/BookingRequest';

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  private apiUrl = 'http://localhost:8080/roompro/'; 
  
  constructor(private http: HttpClient) {}

  submitBooking(booking: BookingResponse):Observable<any>{
    const token = localStorage.getItem('jwtToken');

    if (!token) {
      return throwError(() => ({
        error: 'Please log in to continue.'
      }));
    }

    if(booking.startTime == null || booking.startTime == "" || booking.bookingHours == 0){
      return throwError(() => ({
        error: 'All fields are required.'
      }));
    }


    // Check if booking slot fall between 8AM-6PM

    const startDateTime = new Date(booking.startTime);
    const endDateTime = new Date(startDateTime);
    endDateTime.setHours(startDateTime.getHours() + booking.bookingHours);
    const businessStart = new Date(startDateTime);
    businessStart.setHours(8, 0, 0, 0);  // 08AM
    const businessEnd = new Date(startDateTime);
    businessEnd.setHours(18, 0, 0, 0); // 6PM

    if (
      startDateTime < businessStart || 
      endDateTime > businessEnd ||      
      startDateTime > businessEnd || 
      endDateTime < businessStart   
    ) {
      return throwError(() => ({
        error: 'Booking must be scheduled between 08:00 AM and 06:00 PM.'
      }));
    }




    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,  // Attach token as Bearer Token
      'Content-Type': 'application/json'   // Ensure JSON payload
    });

    // Send POST request with booking data and headers
    return this.http.post(this.apiUrl+"booking", booking, { headers });
  }

  getBookingsByRoomId(token: string, roomId: number):Observable<BookingRequest[]>{
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<BookingRequest[]>(this.apiUrl+"bookings/room/"+roomId, { headers });
  }

}
