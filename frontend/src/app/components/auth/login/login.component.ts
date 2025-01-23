import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LoginModel } from '../../../model/class/LoginModel';
import { LoginService } from '../../../services/auth/login.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  constructor(private router: Router){};

  user: LoginModel = new LoginModel();

  loginService = inject(LoginService);

  onLogin(): void{
    this.loginService.loginUser(this.user).subscribe(
      response => {
        console.log('User logged in successfully:', response.message);
        this.router.navigate(['/home']);
      },
      error => {
        console.error('Registration failed:', error.error?.error || 'Unknown error occurred');
      }
    );
    
  }

  
}
