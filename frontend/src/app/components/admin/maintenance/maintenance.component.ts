import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { MaintenanceService } from '../../../services/maintenance.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MaintenanceFullDetailsRequest } from '../../../model/class/Request/MaintenanceFullDetailsRequest';

@Component({
  selector: 'app-maintenance',
  imports: [CommonModule, FormsModule],
  templateUrl: './maintenance.component.html',
  styleUrl: './maintenance.component.css'
})
export class MaintenanceComponent implements OnInit{

  maintenanceDetails: { roomId: number, name : string, location: string, capacity: number, maintenanceId: number | null, startDate: string | null, endDate: string | null, maintenancePeriod: string | null}[] = [];
  token!: string | null;
  maintenanceService = inject(MaintenanceService);

  constructor(private cdr: ChangeDetectorRef) {}
    

  async ngOnInit(): Promise<void> {
    this.token = localStorage.getItem('jwtToken');

    await Promise.all([
      this.fetchMaitenanceSlots()
    ]);


  }


  fetchMaitenanceSlots(): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.token) {
        this.maintenanceService.fetchAllMaitenanceSlots(this.token).subscribe({
          next: (data) => {
            this.maintenanceDetails = data.map(maintenance => ({
              roomId: maintenance.roomDetails.roomId,
              name: maintenance.roomDetails.name,
              location: maintenance.roomDetails.location, 
              capacity: maintenance.roomDetails.capacity,  
              maintenanceId: maintenance.maintenances!=null ? maintenance.maintenances.maintenanceId : null,
              startDate: maintenance.maintenances!=null ? maintenance.maintenances.startDate : null,  
              endDate: maintenance.maintenances!=null ? maintenance.maintenances.endDate : null, 
              maintenancePeriod: maintenance.maintenances!=null ? this.formatMaintenanceRange(maintenance.maintenances.startDate, maintenance.maintenances.endDate) : null,  
            }));
            
         
            this.maintenanceDetails.sort((a, b) => {
              const order = ['Main', 'East', 'West'];
              const locationComparison =  order.indexOf(a.location.split(' ')[0]) - order.indexOf(b.location.split(' ')[0]);
              if(locationComparison==0){
                return a.name.localeCompare(b.name);
              }
              return locationComparison;
            });

            console.log(this.maintenanceDetails);
            resolve();
          },
          error: (error) => {
            console.error('Error fetching maitenance slots:', error);
            reject(error);
          }
        });
      } else {
        resolve();
      }
    });
  }

  getLocationClass(location: string): string {
    if (!location) return '';
    if (location.startsWith('West')) return 'room-west';
    if (location.startsWith('Main')) return 'room-main';
    if (location.startsWith('East')) return 'room-east';
    return '';
  }

  
  formatMaintenanceRange(startDateStr: string, endDateStr: string): string {
    const startDate = new Date(startDateStr);
    const endDate = new Date(endDateStr);
  
    const startMonth = startDate.toLocaleString('default', { month: 'short' }); // e.g. 'May'
    const endMonth = endDate.toLocaleString('default', { month: 'short' });     // e.g. 'Jun'
  
    const startDay = startDate.getDate(); // e.g. 1
    const endDay = endDate.getDate();     // e.g. 1

    const year = startDate.getFullYear();
    
    if (startMonth === endMonth) {
      return `🛠 ${startMonth} ${startDay}–${endDay} | ${year}`;
    } else {
      return `🛠 ${startMonth} ${startDay} – ${endMonth} ${endDay} | ${year}`;
    }
  }
  
  addMaintenance(room: any): void{
    console.log(room)
  }

  removeMaintenance(room: any): void{
    console.log(room)
  }


}
