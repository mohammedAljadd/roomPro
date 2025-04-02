import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BookingRequest } from '../model/class/Request/BookingRequest';
import { Observable } from 'rxjs';
import { CleaningRequest } from '../model/class/Request/CleaningRequest';
import { CleaningWeeklyRequest } from '../model/class/Request/CleaningWeeklyRequest';

@Injectable({
  providedIn: 'root'
})
export class CleaningService {

  private apiUrl = 'http://localhost:8080/roompro/cleaning/after-use'; 
    
  constructor(private http: HttpClient) {}


  fetchAfterUseCleanings(token: string, roomId: number):Observable<CleaningRequest[]>{
      let params = new HttpParams();
      params = params.set('roomId', roomId.toString());
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return this.http.get<CleaningRequest[]>(this.apiUrl, { params, headers });
    }


    fetchWeeklyCleaning(token: string, roomId: number):Observable<CleaningWeeklyRequest>{
      console.log(roomId);
      let params = new HttpParams();
      params = params.set('roomId', roomId.toString());
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return this.http.get<CleaningWeeklyRequest>('http://localhost:8080/roompro/cleaning/weekly', { params, headers });
    }
}
