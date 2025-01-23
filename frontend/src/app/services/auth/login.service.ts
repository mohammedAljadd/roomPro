import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginModel } from '../../model/class/LoginModel';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  
  private apiUrl = 'http://localhost:8080/roompro/login'; 


  constructor(private http: HttpClient) { }

  loginUser(user: LoginModel): Observable<any> {
    return this.http.post(this.apiUrl, user)
  }
}

