import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RoomRequest } from '../model/class/Request/RoomRequest';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RoomService {

  private apiUrl = 'http://localhost:8080/roompro/meeting-rooms'; // Change to your backend URL

  constructor(private http: HttpClient) {}

  getAllRooms(): Observable<RoomRequest[]> {
    return this.http.get<RoomRequest[]>(this.apiUrl);
  }


  getFilteredRooms(capacity?: number, location?: string, equipment?: string): Observable<RoomRequest[]> {
    let params = new HttpParams();
    if (capacity) params = params.set('capacity', capacity.toString());
    if (location) params = params.set('location', location);
    if (equipment) params = params.set('equipmentNames', equipment);
    
    return this.http.get<RoomRequest[]>('http://localhost:8080/roompro/meeting-rooms/filter', { params });
  }
  
}
