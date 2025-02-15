import { Injectable } from '@angular/core';
import { Booking } from '../model/class/Booking';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { UserBooking } from '../model/class/UserBooking';

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  private apiUrl = 'http://localhost:8080/roompro'; 
  
  constructor(private http: HttpClient) {}

  // Submit a new booking
  submitBooking(booking: Booking): Observable<any> {
    const token = localStorage.getItem('jwtToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.post(this.apiUrl+"/booking", booking, { headers, responseType: 'text' });
  }

  // Fetch all bookings
  getAllBookings(): Observable<UserBooking[]> {
    const token = localStorage.getItem('jwtToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.get<UserBooking[]>(`${this.apiUrl}/all-bookings`, { headers });
  }
}
