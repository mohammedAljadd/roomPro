import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MaintenanceRequest } from '../model/class/Request/MaintenanceRequest';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MaintenanceService {

   private apiUrl = 'http://localhost:8080/roompro/maintenance/get-slots'; 
      
    constructor(private http: HttpClient) {}
  
  
    fetchAMaitenanceSlots(token: string, roomId: number):Observable<MaintenanceRequest[]>{
        let params = new HttpParams();
        params = params.set('roomId', roomId.toString());
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get<MaintenanceRequest[]>(this.apiUrl, { params, headers });
      }
}
