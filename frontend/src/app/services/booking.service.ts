import { Injectable } from '@angular/core';
import { Booking } from '../model/class/Booking';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  private apiUrl = 'http://localhost:8080/roompro/booking'; 
  
  constructor(private http: HttpClient) {}

  submitBooking(booking: Booking):Observable<any>{
    const token = localStorage.getItem('jwtToken');

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,  // Attach token as Bearer Token
      'Content-Type': 'application/json'   // Ensure JSON payload
    });

    // Send POST request with booking data and headers
    return this.http.post(this.apiUrl, booking, { headers, responseType: 'text' });
  }

  
}