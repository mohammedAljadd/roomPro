import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import * as JWT from 'jwt-decode';
import { LoginService } from '../../services/auth/login.service';
import { CleaningService } from '../../services/cleaning.service';
import { CleaningRequestsService } from '../../services/cleaning-requests.service';

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
  isAdmin: boolean = false;

  pendingCount: number = 0;

  loginService = inject(LoginService);
  cleaningService = inject(CleaningService);
  cleaningStateService = inject(CleaningRequestsService);

  ngOnInit(): void {
    
    this.loginService.getToken().subscribe(token => {
      this.jwtToken = token;
      if (token) {
        this.userName = this.decodeToken(token).firstName;
        this.isAdmin = this.decodeToken(token).role == "Admin";
        
      } else {
        this.userName = null;
      }
      this.fetchPendingCount();
    });

    

    this.cleaningStateService.pendingCountUpdated$.subscribe(() => {
      this.fetchPendingCount();
    });
  }

  decodeToken(token: string) {
    
    return JSON.parse(atob(token.split('.')[1]));
  }

  logout(): void{
    this.loginService.logout();
    this.router.navigate(['/home']);
  }

  fetchPendingCount() {
    if(this.jwtToken){
      this.cleaningService.getCleaningRequests(this.jwtToken).subscribe(requests => {
        this.pendingCount = requests.filter(request=>request.status=='ON_HOLD').length;
      });
    }
    
  }


}
