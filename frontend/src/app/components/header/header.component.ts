import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, ElementRef, HostListener, inject, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { LoginService } from '../../services/auth/login.service';
import { CleaningService } from '../../services/cleaning.service';
import { CleaningRequestsService } from '../../services/cleaning-requests.service';
import { NotificationcleaningrequestService } from '../../services/notificationcleaningrequest.service';
import { CleaningOnRequest } from '../../model/class/Request/CleaningOnRequest';

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, CommonModule, FormsModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

  
  constructor(private router: Router, private cdr: ChangeDetectorRef){};

  jwtToken: string | null = null;
  userName: string | null = null;
  isAdmin: boolean = false;

  pendingCount: number = 0;
  @ViewChild('notifContainer') notifContainer!: ElementRef;
  @HostListener('document:click', ['$event'])
  handleClickOutside(event: MouseEvent) {
    if (this.showNotifications && this.notifContainer && !this.notifContainer.nativeElement.contains(event.target)) {
      this.showNotifications = false;
    }
  }
  loginService = inject(LoginService);
  cleaningService = inject(CleaningService);
  cleaningStateService = inject(CleaningRequestsService);
  notificationCleaningRequest = inject(NotificationcleaningrequestService);

  userCleaningRequest: CleaningOnRequest[] = [];

  showNotifications = false;

  notifications: string[] = [];

  notViewedRequestCount: number = 0;

  private decodeToken(token: string): { firstName?: string; role?: string } | null {
    try {
      return jwtDecode<{ firstName?: string; role?: string }>(token);
    } catch (error) {
      console.error('Invalid token payload:', error);
      return null;
    }
  }

  private hasAdminRole(decoded: any): boolean {
    if (!decoded) return false;

    const normalize = (value: string) => value.toLowerCase().replace(/^role_/, '');

    if (typeof decoded.role === 'string') {
      return normalize(decoded.role) === 'admin';
    }

    if (Array.isArray(decoded.roles)) {
      return decoded.roles.some((value: unknown) => typeof value === 'string' && normalize(value) === 'admin');
    }

    if (Array.isArray(decoded.authorities)) {
      return decoded.authorities.some((value: unknown) => typeof value === 'string' && normalize(value) === 'admin');
    }

    return false;
  }

  ngOnInit(): void {
    
    this.loginService.getToken().subscribe(token => {
      this.jwtToken = token;
      if (token) {
        const decoded = this.decodeToken(token);
        this.userName = decoded?.firstName ?? null;
        this.isAdmin = this.hasAdminRole(decoded);
      } else {
        this.userName = null;
        this.isAdmin = false;
      }
      this.fetchPendingCount();
      this.fetchNotViewedRequest();
      this.cdr.detectChanges();
    });

    

    this.cleaningStateService.pendingCountUpdated$.subscribe(() => {
      this.fetchPendingCount();
    });
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

  fetchNotViewedRequest(){
    if(this.jwtToken){
      this.cleaningService.getProcessedRequest(this.jwtToken).subscribe({
        next: (data)=>{
          
          this.userCleaningRequest = data;
          this.notViewedRequestCount = this.userCleaningRequest.length;

          this.userCleaningRequest = this.userCleaningRequest.sort((a, b)=>
            new Date(b.requestedAt).getTime()-new Date(a.requestedAt).getTime());
          
          


          this.notifications = [];
          for(let i=0; i<this.userCleaningRequest.length; i++){
            
            let req = this.userCleaningRequest[i];
            let date = req.requestedAt;
            date = date.split('T')[0]+" at "+date.split('T')[1].substring(0, 5)
            let status = req.status=="ACCEPTED"?'approved.':'declined.';
            this.notifications.push(
              "Your cleaning request for " + req.room.name + ", submitted on "+ date + ", has been " + status
            )
          }
        },
        error: (error)=>{
          console.log(error);
        }
      });

      

    }
  }

  removeNotification(index: number){
    this.notifications.splice(index, 1);
    let cleaningId = this.userCleaningRequest[index].cleaningId;
    if(this.jwtToken){
      this.cleaningService.markProcessedRequestAsViewed(this.jwtToken, cleaningId).subscribe({
        next: response=>{
          this.fetchNotViewedRequest(); 
          this.cdr.detectChanges()
        },
        error: error=> {}
      })

    }
    
    this.fetchNotViewedRequest();
    this.cdr.detectChanges()
  }

  toggleNotifications() {
    this.showNotifications = !this.showNotifications;
  }

  notifClass(i: number){
    if(this.userCleaningRequest[i].status=="ACCEPTED"){
      return "accepted";
    }
    else{
      return "rejected";
    }
  }

}
