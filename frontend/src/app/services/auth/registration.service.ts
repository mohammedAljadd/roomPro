import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { UserRegistrationResponse } from '../../model/class/Response/UserRegistrationResponse';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  private apiUrl = 'http://localhost:8080/roompro/register'; 

  constructor(private http: HttpClient) {}

  registerUser(user: UserRegistrationResponse): Observable<any> {


    const isAnyFieldEmpty = Object.values(user).some(value => value == null || value === "");
    if (isAnyFieldEmpty) {
      return throwError(() => ({
        error: 'All fields are required.'
      }));
    }


    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(user.email)) {
      return throwError(() => ({
        error: 'Please enter a valid email address.'
      }));
    }

    
    return this.http.post(this.apiUrl, user)
  }
}
