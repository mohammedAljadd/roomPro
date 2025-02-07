import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import * as JWT from 'jwt-decode';
import { LoginService } from '../../services/auth/login.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, CommonModule, FormsModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

  
  constructor(private router: Router){};

  jwtToken: string | null = null;
  userName: string | null = null;

  loginService = inject(LoginService);

  ngOnInit(): void {
    // Subscribe to token changes
    this.loginService.getToken().subscribe(token => {
      this.jwtToken = token;
      if (token) {
        this.userName = this.decodeToken(token).firstName;
      } else {
        this.userName = null;
      }
    });
  }

  decodeToken(token: string) {
    // Decode the JWT token (you can use jwt-decode library here)
    return JSON.parse(atob(token.split('.')[1]));
  }

  logout(): void{
    this.loginService.logout();
    this.router.navigate(['/home']);
  }
}
