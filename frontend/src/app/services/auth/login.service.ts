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
    return this.http.post<{ token: string }>(this.apiUrl, user)
  }


  saveToken(token: string) {
    localStorage.setItem('jwtToken', token);
  }

  getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout() {
    localStorage.removeItem('jwtToken');
  }
}

