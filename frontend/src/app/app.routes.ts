import { Routes } from '@angular/router';
import { RegisterComponent } from './components/auth/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/auth/login/login.component';
import { MeetingroomsComponent } from './components/meetingrooms/meetingrooms.component';
import { MybookingsComponent } from './components/mybookings/mybookings.component';
import { RoomcallendarComponent } from './components/roomcallendar/roomcallendar.component';
import { ManageroomsComponent } from './components/managerooms/managerooms.component';


export const routes: Routes = [
    {path: 'home', component: HomeComponent},
    {path: 'meeting-rooms', component: MeetingroomsComponent},
    {path: 'meeting-rooms/:id/:roomName', component: RoomcallendarComponent},
    {path: 'register', component: RegisterComponent},
    {path: 'login', component: LoginComponent},
    {path: 'my-bookings', component: MybookingsComponent},
    {path: 'manage-rooms', component: ManageroomsComponent},
    { path: '**', redirectTo: 'home' }, 
];
