import { Injectable } from '@angular/core';
import { Booking } from '../model/class/Booking';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { UserBooking } from '../model/class/UserBooking';

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  private apiUrl = 'http://localhost:8080/roompro/'; 
  
  constructor(private http: HttpClient) {}

  submitBooking(booking: Booking):Observable<any>{
    const token = localStorage.getItem('jwtToken');

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,  // Attach token as Bearer Token
      'Content-Type': 'application/json'   // Ensure JSON payload
    });

    // Send POST request with booking data and headers
    return this.http.post(this.apiUrl+"booking", booking, { headers, responseType: 'text' });
  }

  getBookingsByRoomId(token: string, roomId: number):Observable<UserBooking[]>{
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<UserBooking[]>(this.apiUrl+"bookings/room/"+roomId, { headers });
  }



  
}
