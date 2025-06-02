import { Routes } from '@angular/router';
import { RegisterComponent } from './components/auth/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/auth/login/login.component';
import { MeetingroomsComponent } from './components/meetingrooms/meetingrooms.component';
import { MybookingsComponent } from './components/mybookings/mybookings.component';
import { RoomcallendarComponent } from './components/roomcallendar/roomcallendar.component';
import { AddRemoveEquipmentComponent } from './components/admin/addremoveequipment/addremoveequipment.component';
import { ManageroomsComponent } from './components/admin/managerooms/managerooms.component';
import { RequestcleaningComponent } from './components/requestcleaning/requestcleaning.component';
import { UserrequestedcleaningComponent } from './components/userrequestedcleaning/userrequestedcleaning.component';
import { Component } from '@angular/core';
import { HeaderComponent } from './components/header/header.component';


export const routes: Routes = [
    {path: 'home', component: HomeComponent},
    {path: 'meeting-rooms', component: MeetingroomsComponent},
    {path: 'meeting-rooms/:id/:roomName', component: RoomcallendarComponent},
    {path: 'register', component: RegisterComponent},
    {path: 'login', component: LoginComponent},
    {path: 'my-bookings', component: MybookingsComponent},
    {path: 'add-remove-equipment', component: AddRemoveEquipmentComponent},
    {path: 'manage-rooms', component: ManageroomsComponent},
    {path: 'request-cleaning', component: RequestcleaningComponent},
    {path: 'requested-cleaning', component: UserrequestedcleaningComponent},
    { path: '**', redirectTo: 'home' }, 
];
