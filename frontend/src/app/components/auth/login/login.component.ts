import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LoginService } from '../../../services/auth/login.service';
import { Router } from '@angular/router';
import { UserLoginResponse } from '../../../model/class/Response/UserLoginResponse';
import { ToastnotificationService } from '../../../services/toastnotification.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  constructor(private router: Router, private cdr: ChangeDetectorRef){};

  user: UserLoginResponse = new UserLoginResponse();

  loginService = inject(LoginService);

  toastNotif = inject(ToastnotificationService);



  onLogin(): void{
    this.loginService.loginUser(this.user).subscribe(
      response => {
        this.loginService.saveToken(response.token);
        this.cdr.detectChanges();
        this.toastNotif.showSuccess('You have successfully logged in!', 'Login Success');

        this.router.navigate(['/home']);
      },
      error => {
        this.toastNotif.showError(error.error, 'Login Error');
      }
    );
    
  }

  
}
