import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MaintenanceRequest } from '../model/class/Request/MaintenanceRequest';
import { Observable } from 'rxjs';
import { MaintenanceFullDetailsRequest } from '../model/class/Request/MaintenanceFullDetailsRequest';

@Injectable({
  providedIn: 'root'
})
export class MaintenanceService {

   private apiUrl = 'http://localhost:8080/roompro/maintenance/'; 
      
    constructor(private http: HttpClient) {}
  
  
    fetchAMaitenanceSlots(token: string, roomId: number):Observable<MaintenanceRequest[]>{
        let params = new HttpParams();
        params = params.set('roomId', roomId.toString());
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get<MaintenanceRequest[]>(this.apiUrl+"get-slots", { params, headers });
      }


    fetchAllMaitenanceSlots(token: string):Observable<MaintenanceFullDetailsRequest[]>{
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return this.http.get<MaintenanceFullDetailsRequest[]>(this.apiUrl+"get-slots/all", {headers});
    }


    removeMaintenance(token: string, maintenanceId: number): Observable<{ message: string }> {
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return this.http.delete<{ message: string }>(this.apiUrl+"delete/"+maintenanceId, {headers});
    }

    addMaintenance(token: string, maintenance: MaintenanceRequest): Observable<{message: string }>{
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return this.http.post<{ message: string }>(this.apiUrl+"add", maintenance, {headers});
    }
}
