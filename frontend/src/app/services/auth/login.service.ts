import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { LoginModel } from '../../model/class/LoginModel';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  
  private apiUrl = 'http://localhost:8080/roompro/login'; 
  private jwtTokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);


  constructor(private http: HttpClient) { 
    const token = localStorage.getItem('jwtToken');
    this.jwtTokenSubject.next(token);
  }

  

  loginUser(user: LoginModel): Observable<any> {
    return this.http.post<{ token: string }>(this.apiUrl, user)
  }


  saveToken(token: string) {
    localStorage.setItem('jwtToken', token);
    this.jwtTokenSubject.next(token);
  }

  getToken() {
    return this.jwtTokenSubject.asObservable();
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout() {
    localStorage.removeItem('jwtToken');
    this.jwtTokenSubject.next(null);
  }
}

