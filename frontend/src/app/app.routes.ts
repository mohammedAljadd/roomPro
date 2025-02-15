import { Routes } from '@angular/router';
import { RegisterComponent } from './components/auth/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/auth/login/login.component';
import { MeetingroomsComponent } from './components/meetingrooms/meetingrooms.component';
import { MybookingsComponent } from './components/mybookings/mybookings.component';
import { CalendarComponent } from './components/calendar/calendar.component';


export const routes: Routes = [
    {path: 'home', component: HomeComponent},
    {path: 'meeting-rooms', component: MeetingroomsComponent},
    {path: 'register', component: RegisterComponent},
    {path: 'login', component: LoginComponent},
    {path: 'my-bookings', component: MybookingsComponent},
    {path: 'calendar', component: CalendarComponent},
    { path: '**', redirectTo: 'home' }, 
];
