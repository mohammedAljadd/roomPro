import { Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { RegisterComponent } from './components/auth/register/register.component';


export const routes: Routes = [
    {path: '', component: AppComponent},
    {path: 'register', component: RegisterComponent}
];
