import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EquipementRequest } from '../model/class/Request/EquipementRequest';

@Injectable({
  providedIn: 'root'
})
export class EquipementService {

  constructor(private http: HttpClient) { }

  private apiUrl = "http://localhost:8080/roompro/equipments";

  fetAllEquipements(): Observable<EquipementRequest[]> {
    
      return this.http.get<EquipementRequest[]>(this.apiUrl);
    }

    
}
