import { Component } from '@angular/core';
import { RegistrationModel } from '../../../model/class/RegistrationModel';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  user: RegistrationModel = new RegistrationModel();

  onLogin(){
    
  }
}
