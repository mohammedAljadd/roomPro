import { Component, inject, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RegistrationService } from '../../../services/auth/registration.service';
import { RegistrationModel } from '../../../model/class/RegistrationModel';

@Component({
  selector: 'app-register',
  imports: [FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
  encapsulation: ViewEncapsulation.None
})
export class RegisterComponent {
  

  registrationService = inject(RegistrationService);

  user: RegistrationModel = new RegistrationModel();

  onRegisterUser(): void {
    this.registrationService.registerUser(this.user).subscribe(
      response => {
        console.log('User registered successfully:', response);
      },
      error => {
        console.error('Registration failed', error);
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

  

