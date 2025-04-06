import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HolidayService {


  private apiUrl = 'https://date.nager.at/api/v3/publicholidays/'; // 2025/FR


  constructor(private http: HttpClient) { }


  fetHolidays(year: string, country: string): Observable<any>{
    return this.http.get<any>(this.apiUrl+year+"/"+country);
  }



}
