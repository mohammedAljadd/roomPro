import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
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


  fetchAfterUseCleanings(token: string, roomId: number):Observable<CleaningRequest[]>{
      let params = new HttpParams();
      params = params.set('roomId', roomId.toString());
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return this.http.get<CleaningRequest[]>(this.apiUrl, { params, headers });
    }
}
