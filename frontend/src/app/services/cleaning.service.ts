import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BookingRequest } from '../model/class/Request/BookingRequest';
import { Observable } from 'rxjs';
import { CleaningRequest } from '../model/class/Request/CleaningRequest';
import { CleaningWeeklyRequest } from '../model/class/Request/CleaningWeeklyRequest';
import { CleaningOnRequest } from '../model/class/Request/CleaningOnRequest';

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
      let params = new HttpParams();
      params = params.set('roomId', roomId.toString());
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return this.http.get<CleaningWeeklyRequest>('http://localhost:8080/roompro/cleaning/weekly', { params, headers });
    }


  requestCleaning(token: string, roomId: number, message: string):Observable<{message: string}>{
    const request = {
      roomId: roomId,
      message: message,
      requestedAt: new Date().toISOString().split('.')[0]
    };
    console.log(request);
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post<{message: string}>('http://localhost:8080/roompro/cleaning/request', request, { headers });
  }


  getCleaningRequests(token: string):Observable<CleaningOnRequest[]>{
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<CleaningOnRequest[]>('http://localhost:8080/roompro/cleaning/request/get', { headers });
  }

  getProcessedRequest(token: string):Observable<CleaningOnRequest[]>{
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<CleaningOnRequest[]>('http://localhost:8080/roompro/cleaning/request/get/processed', { headers });
  }

  markProcessedRequestAsViewed(token: string, cleaning_id:number):Observable<void>{
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.patch<void>('http://localhost:8080/roompro/cleaning/request/processed/marked-view/'+cleaning_id, {}, { headers });
  }

  acceptRequest(token: string, cleaningId: number, startTime: string, endTime: string):Observable<{message: string}>{

    const statusData = {
      cleaningId: cleaningId,
      status: "ACCEPTED",
      startTime : startTime,
      endTime : endTime,
    }

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.patch<{message: string}>('http://localhost:8080/roompro/cleaning/request/set_status', statusData, { headers });
  }

  rejectRequest(token: string, cleaningId: number):Observable<{message: string}>{

    const statusData = {
      cleaningId: cleaningId,
      status: "REJECTED",
      startTime : null,
      endTime : null,
    }

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.patch<{message: string}>('http://localhost:8080/roompro/cleaning/request/set_status', statusData, { headers });
  }

}
