import { Routes } from '@angular/router';
import { RegisterComponent } from './components/auth/register/register.component';
import { HomeComponent } from './components/home/home.component';


export const routes: Routes = [
    {path: 'home', component: HomeComponent},
    {path: 'register', component: RegisterComponent},
    { path: '**', redirectTo: 'home' }, 
];
