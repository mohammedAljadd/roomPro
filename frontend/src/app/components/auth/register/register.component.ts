import { Component, inject, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RegistrationService } from '../../../services/auth/registration.service';
import { Router } from '@angular/router';
import { UserRegistrationResponse } from '../../../model/class/Response/UserRegistrationResponse';

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

  onRegisterUser(): void {
    this.registrationService.registerUser(this.user).subscribe(
      response => {
        this.router.navigate(['/login']);
      },
      error => {
        console.log("Error how "+error.error );
        console.error('Registration failed:', error.error || 'Unknown error occurred');
      }
    );
  }


      resetForm() {
        this.user = {
          firstName: '',
          lastName: '',
          email: '',
          password: '',
          roleName: ''
        };
      }
    }

  

