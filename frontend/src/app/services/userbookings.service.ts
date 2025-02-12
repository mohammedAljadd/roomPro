import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserBooking } from '../model/class/UserBooking';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserbookingsService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'http://localhost:8080/roompro/my-bookings';

  getUserBookings(token: string): Observable<UserBooking[]> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<UserBooking[]>(this.apiUrl, { headers });
  }

}
