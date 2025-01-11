import { Component, inject, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RegistrationService } from '../../../services/auth/registration.service';
import { RegistrationModel } from '../../../model/class/RegistrationModel';
import { Router } from '@angular/router';

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

  user: RegistrationModel = new RegistrationModel();

  onRegisterUser(): void {
    this.registrationService.registerUser(this.user).subscribe(
      response => {
        console.log('User registered successfully:', response.message);
        this.router.navigate(['/login']);
      },
      error => {
        console.error('Registration failed:', error.error?.error || 'Unknown error occurred');
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

  

