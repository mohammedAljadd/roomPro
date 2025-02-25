import { Component, inject, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RegistrationService } from '../../../services/auth/registration.service';
import { Router } from '@angular/router';
import { UserRegistrationResponse } from '../../../model/class/Response/UserRegistrationResponse';
import { ToastnotificationService } from '../../../services/toastnotification.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
  encapsulation: ViewEncapsulation.None
})
export class RegisterComponent {
  
  constructor(private router: Router){};

  registrationService = inject(RegistrationService);

  user: UserRegistrationResponse = new UserRegistrationResponse();

  toastNotif = inject(ToastnotificationService);

  onRegisterUser(): void {
    this.registrationService.registerUser(this.user).subscribe(
      response => {
        this.router.navigate(['/login']);
        this.toastNotif.showSuccess('Registration successful! You can now log in.', 'Registration Successful');
        
      },
      error => {
        this.toastNotif.showError(error.error, 'Registration failed');
      }
    );
  }


      resetForm() {
        this.user = {
          firstName: '',
          lastName: '',
          email: '',
          password: ''
        };
      }
    }

  

