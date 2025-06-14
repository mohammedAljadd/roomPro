import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { UserLoginResponse } from '../../model/class/Response/UserLoginResponse';
import { UsersStatsRequest } from '../../model/class/Request/UsersStatsRequest';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  
  private apiUrl = 'http://localhost:8080/roompro/'; 
  private jwtTokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);


  constructor(private http: HttpClient) { 
    const token = localStorage.getItem('jwtToken');
    this.jwtTokenSubject.next(token);
  }

  

  loginUser(user: UserLoginResponse): Observable<any> {

    if(user.email == null || user.email == "" || user.password == null || user.password == null){
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

    return this.http.post<{ token: string }>(this.apiUrl+"login", user)
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


  getUsersStats(token: string, year: number, month: number): Observable<UsersStatsRequest>{
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<UsersStatsRequest>(this.apiUrl+"users/stats/"+year+"/"+month, { headers });
  }
}

