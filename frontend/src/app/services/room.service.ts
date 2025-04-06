import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RoomRequest } from '../model/class/Request/RoomRequest';
import { Observable } from 'rxjs';
import { NewRoomResponse } from '../model/class/Response/NewRoomResponse';
import { RoomCleaningRequest } from '../model/class/Request/RoomCleaningRequest';
import { RoomSetCleaningResponse } from '../model/class/Response/RoomSetCleaningResponse';

@Injectable({
  providedIn: 'root'
})
export class RoomService {

  private apiUrl = 'http://localhost:8080/roompro/meeting-rooms';

  constructor(private http: HttpClient) {}

  getAllRooms(): Observable<RoomRequest[]> {
    return this.http.get<RoomRequest[]>(this.apiUrl);
  }

  getRoomsWithCleaningType(token: string): Observable<RoomCleaningRequest[]>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'   
    });
    return this.http.get<RoomCleaningRequest[]>(this.apiUrl+"/cleaning", {headers})
  }


  getFilteredRooms(capacity?: number, location?: string, equipment?: string): Observable<RoomRequest[]> {
    let params = new HttpParams();
    if (capacity) params = params.set('capacity', capacity.toString());
    if (location) params = params.set('location', location);
    if (equipment) params = params.set('equipmentNames', equipment);
    
    return this.http.get<RoomRequest[]>('http://localhost:8080/roompro/meeting-rooms/filter', { params });
  }

  setCleaningType(roomId: number, cleaningId: number, previousCleaningId:number, token: string): Observable<{ message: string }>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,  // Attach token as Bearer Token
      'Content-Type': 'application/json'   // Ensure JSON payload
    });

    let params = new HttpParams();
    params = params.set('roomId', roomId.toString());
    params = params.set('cleaningId', cleaningId.toString());
    params = params.set('previousCleaningId', previousCleaningId.toString());
    return this.http.get<{ message: string}>('http://localhost:8080/roompro/meeting-rooms/cleaning/update', { params , headers});
  }



  addNewRoom(room: NewRoomResponse, token:string): Observable<{ message: string }>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,  // Attach token as Bearer Token
      'Content-Type': 'application/json'   // Ensure JSON payload
    });
    return this.http.post<{ message: string }>('http://localhost:8080/roompro/add-meeting-rooms', room, {headers})

  }

  
  deleteRoom(token: string, roomId: number): Observable<{ message: string }> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.delete<{ message: string }>(this.apiUrl+"/delete/"+roomId, {headers});
  }
}
