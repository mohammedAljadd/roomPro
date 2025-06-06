import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AddroomsComponent } from "../addrooms/addrooms.component";
import { DeleteroomComponent } from "../deleteroom/deleteroom.component";
import { CleaningperiodsComponent } from "../cleaningperiods/cleaningperiods.component";
import { MaintenanceComponent } from "../maintenance/maintenance.component";
import { UserstatsComponent } from "../userstats/userstats.component";
import { BookingtrendsComponent } from "../bookingtrends/bookingtrends.component";

@Component({
  selector: 'app-managerooms',
  imports: [CommonModule, AddroomsComponent, DeleteroomComponent, CleaningperiodsComponent, MaintenanceComponent, UserstatsComponent, BookingtrendsComponent],
  templateUrl: './managerooms.component.html',
  styleUrl: './managerooms.component.css'
})
export class ManageroomsComponent implements OnInit{
  
  choosenItem: string | null = null;
  
  ngOnInit(): void {
    this.choosenItem = "userstats";
  }


  setChoosenItem(item: string){
    this.choosenItem=item;
  }




}
