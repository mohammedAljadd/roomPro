import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { MaintenanceService } from '../../../services/maintenance.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MaintenanceFullDetailsRequest } from '../../../model/class/Request/MaintenanceFullDetailsRequest';
import { ToastnotificationService } from '../../../services/toastnotification.service';
import { MaintenanceRequest } from '../../../model/class/Request/MaintenanceRequest';
import { BookingService } from '../../../services/booking.service';

@Component({
  selector: 'app-maintenance',
  imports: [CommonModule, FormsModule],
  templateUrl: './maintenance.component.html',
  styleUrl: './maintenance.component.css'
})
export class MaintenanceComponent implements OnInit{

  maintenanceDetails: { roomId: number, name : string, location: string, capacity: number, maintenanceId: number | null, startDate: string | null, endDate: string | null, maintenancePeriod: string | null}[] = [];
 
  selectedRoom: { roomName: string, location: string, capacity: number, startDate: string, endDate: string} = {
    roomName: '',
    location: '',
    capacity: 0,
    startDate: '',
    endDate: ''
  };
  

  roomBookings: { start: Date; end: Date}[] = [];

  maintenanceRequest: MaintenanceRequest = {
    maintenanceId: 0,
    roomId: 0,
    startDate: "",
    endDate: "",
  };

  selectedMaintenanceId!: number;
  

  toastNotif = inject(ToastnotificationService);

  bookingService = inject(BookingService);

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
  
  addMaintenanceModal(room: any): void{
    this.selectedRoom.roomName = room.name;
    this.maintenanceRequest.roomId = room.roomId;
    this.fetchBookings(room.roomId);
    const modal = new window.bootstrap.Modal(document.getElementById('AddMaintenanceModal'));
    modal.show();
  }

  addMaintenance(): void{
    if (this.token) {
      this.maintenanceService.addMaintenance(this.token, this.maintenanceRequest).subscribe(
        {
          next: response => {
            // Refresh all data after successful booking
            this.fetchMaitenanceSlots();
            this.cdr.detectChanges();
            this.toastNotif.showSuccess(response.message);
          },
          error: error => {
            this.toastNotif.showError(error.error);
          }
        }
      );
    }

    document.body.focus();

    const modalElement = document.getElementById('AddMaintenanceModal');
    if (modalElement) {
      const modal = window.bootstrap.Modal.getInstance(modalElement);
      if (modal) {
        modal.hide();
      }
    }
  }


  cancelMaintenanceModal(room: any): void{
    this.selectedRoom.roomName = room.name;
    this.selectedRoom.capacity = room.capacity;
    this.selectedRoom.location = room.location;
    this.selectedRoom.startDate = room.startDate;
    this.selectedRoom.endDate = room.endDate;
    this.selectedMaintenanceId = room.maintenanceId;
    const modal = new window.bootstrap.Modal(document.getElementById('cancelMaintenanceModal'));
    modal.show();
  }




  removeMaintenance(): void{
    if (this.token) {
      
      this.maintenanceService.cancelMaintenance(this.token, this.selectedMaintenanceId).subscribe({
        next: response  => {
          this.fetchMaitenanceSlots();
          this.toastNotif.showSuccess(response.message);
          this.cdr.detectChanges();
          
        },
        error: error => {
          this.toastNotif.showError(error.error);
        }
      });
    } else {
      console.log('No token found');
    }

    document.body.focus();

    
    const modalElement = document.getElementById('cancelMaintenanceModal');
    if (modalElement) {
      const modal = window.bootstrap.Modal.getInstance(modalElement);
      if (modal) {
        modal.hide();
      }
    }
  }

  fetchBookings(roomId: number): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.token) {
        this.bookingService.getBookingsByRoomId(this.token, roomId).subscribe({
          next: (data) => {
            this.roomBookings = data.map(booking => ({
              start: new Date(booking.startTime),
              end: new Date(booking.endTime)
            }));
            resolve();
          },
          error: (error) => {
            console.error('Error fetching bookings:', error);
            reject(error);
          }
        });
      } else {
        resolve();
      }
    });
  }


}
