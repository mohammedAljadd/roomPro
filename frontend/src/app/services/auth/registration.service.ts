import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserRegistrationResponse } from '../../model/class/Response/UserRegistrationResponse';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  private apiUrl = 'http://localhost:8080/roompro/register'; 

  constructor(private http: HttpClient) {}

  registerUser(user: UserRegistrationResponse): Observable<any> {
    return this.http.post(this.apiUrl, user)
  }
}
