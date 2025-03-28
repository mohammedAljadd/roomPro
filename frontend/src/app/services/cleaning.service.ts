import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BookingRequest } from '../model/class/Request/BookingRequest';
import { Observable } from 'rxjs';
import { CleaningRequest } from '../model/class/Request/CleaningRequest';

@Injectable({
  providedIn: 'root'
})
export class CleaningService {

  private apiUrl = 'http://localhost:8080/roompro/cleaning/after-use'; 
    
  constructor(private http: HttpClient) {}


  fetchAfterUseCleanings(token: string):Observable<CleaningRequest[]>{
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return this.http.get<CleaningRequest[]>(this.apiUrl, { headers });
    }
}
