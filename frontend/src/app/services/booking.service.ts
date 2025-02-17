import { Injectable } from '@angular/core';
import { BookingResponse } from '../model/class/Response/BookingResponse';
import { Observable } from 'rxjs';
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

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,  // Attach token as Bearer Token
      'Content-Type': 'application/json'   // Ensure JSON payload
    });

    // Send POST request with booking data and headers
    return this.http.post(this.apiUrl+"booking", booking, { headers, responseType: 'text' });
  }

  getBookingsByRoomId(token: string, roomId: number):Observable<BookingRequest[]>{
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<BookingRequest[]>(this.apiUrl+"bookings/room/"+roomId, { headers });
  }

}
