import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BookingRequest } from '../model/class/Request/BookingRequest';

@Injectable({
  providedIn: 'root'
})
export class UserbookingsService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8080/roompro/my-bookings';

  getUserBookings(token: string): Observable<BookingRequest[]> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<BookingRequest[]>(this.apiUrl, { headers });
  }

  cancelBooking(token: string, bookingId: number): Observable<{ message: string }> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.delete<{ message: string }>(this.apiUrl+"/cancel/"+bookingId, {headers});
  }

}
