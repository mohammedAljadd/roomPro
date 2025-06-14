import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EquipementRequest } from '../model/class/Request/EquipementRequest';
import { EquipmentUpdateResponse } from '../model/class/Response/EquipmentUpdateResponse';

@Injectable({
  providedIn: 'root'
})
export class EquipementService {

  constructor(private http: HttpClient) { }

  private apiUrl = "http://localhost:8080/roompro/equipments";
  
  fetAllEquipements(token: string, roomId: number): Observable<EquipementRequest[]> {
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      let params = new HttpParams();
      if (roomId) params = params.set('roomId', roomId.toString());
      return this.http.get<EquipementRequest[]>(this.apiUrl, { headers, params });
    }

    updateEquipements(token: string, equipmentsUpdate:EquipmentUpdateResponse[] ): Observable<{message: string}> {
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return this.http.post<{message: string}>(this.apiUrl+"-update", equipmentsUpdate, { headers });
    }

    
}
