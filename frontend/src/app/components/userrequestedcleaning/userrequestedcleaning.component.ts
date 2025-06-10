import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CleaningOnRequest } from '../../model/class/Request/CleaningOnRequest';
import { CleaningService } from '../../services/cleaning.service';
import { ToastnotificationService } from '../../services/toastnotification.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingService } from '../../services/booking.service';
import { CleaningRequestsService } from '../../services/cleaning-requests.service';

@Component({
  selector: 'app-userrequestedcleaning',
  imports: [CommonModule, FormsModule],
  templateUrl: './userrequestedcleaning.component.html',
  styleUrl: './userrequestedcleaning.component.css'
})
export class UserrequestedcleaningComponent implements OnInit{


  constructor(private cdr: ChangeDetectorRef){}

  cleaningRequests: CleaningOnRequest[] = [];
  cleaningService = inject(CleaningService);
  toastNotif = inject(ToastnotificationService);
  bookingService = inject(BookingService);
  cleaningStateService = inject(CleaningRequestsService);

  selectedRequest!: CleaningOnRequest;

  roomBookings: { start: Date; end: Date}[] = [];

  startDate!: Date;
  cleaningDuration!: number;
  adminComment!: string;
  

  token!: string | null;

  ngOnInit(): void {
      this.token = localStorage.getItem('jwtToken');
      this.fetchPendingRequests();
  }


  fetchPendingRequests():Promise<void>  {
    return new Promise((resolve, reject) => {
    if(this.token){
      this.cleaningService.getCleaningRequests(this.token).subscribe({
        next: (data)=>{


          this.cleaningRequests = data;

          console.log(this.cleaningRequests);

          this.cleaningRequests.sort((req1, req2) => {
            return new Date(req2.requestedAt).getTime() - new Date(req1.requestedAt).getTime();
          });



          this.cleaningRequests.map(request=>{
            if(request.status=='ON_HOLD'){
              request.status = 'ON HOLD';
            }

            let len = request.requestedAt.length;
            request.requestedAt = request.requestedAt.substring(0, len-3);
            if(request.startTime && request.endTime){
              request.startTime = request.startTime.split('T')[0]+ ' ' + request.startTime.split('T')[1];
              request.startTime = request.startTime.substring(0, len-3);
              request.endTime = request.endTime.split('T')[0]+ ' ' + request.endTime.split('T')[1];
              request.endTime = request.endTime.substring(0, len-3);
              
            }

            request.requestedAt = request.requestedAt.split('T')[0] + ' ' + request.requestedAt.split('T')[1];
          })

          resolve();

        },
        error: (error)=>{
          console.log(error);
        }
      });
    } else {
      resolve();
    }
  });
}


  accepteRequestModal(request: CleaningOnRequest): void{
    this.selectedRequest = request;
    this.fetchBookings(request.room.roomId);
    const modal = new window.bootstrap.Modal(document.getElementById('SetCleaningStatuseModal'));
    modal.show();
  }

 

  acceptRequest(): void{
    const startDateObj = new Date(this.startDate);
    
    
    const endDateObj = new Date(startDateObj.getTime() + this.cleaningDuration * 60000);

    
    const formatForBackend = (date: Date) => {
      const pad = (n: number) => n.toString().padStart(2, '0');
      return `${date.getFullYear()}-${pad(date.getMonth()+1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:00`;
    };

    const startTime = formatForBackend(startDateObj);
    const endTime = formatForBackend(endDateObj); 


    const modalElement = document.getElementById('SetCleaningStatuseModal');
    

    if(this.token){
      
      this.cleaningService.acceptRequest(this.token, this.selectedRequest.cleaningId, startTime, endTime, this.adminComment).subscribe({
        next: response => {
          this.toastNotif.showSuccess(response.message);

          this.fetchPendingRequests().then(() => {
            this.cleaningStateService.notifyPendingCountChanged();
            this.fetchBookings(this.selectedRequest.room.roomId).then(() => {
              this.cdr.detectChanges();
            });
          });
          if (modalElement) {
            const modal = window.bootstrap.Modal.getInstance(modalElement);
            if (modal) {
              modal.hide();
            }
          }
        },
        error: error => {
          this.toastNotif.showError(error.error);
        }
      })
    }
    
    
    
    
  }

  rejectRequestModal(request: CleaningOnRequest): void{
    this.selectedRequest = request;
    const modal = new window.bootstrap.Modal(document.getElementById('rejectCleaningRequestModal'));
    modal.show();
  }


  rejectRequest(){
    const modalElement = document.getElementById('rejectCleaningRequestModal');

    if(this.token){
      this.cleaningService.rejectRequest(this.token, this.selectedRequest.cleaningId).subscribe({
        next: response => {
          this.toastNotif.showSuccess(response.message);

          this.fetchPendingRequests().then(() => {
            this.cleaningStateService.notifyPendingCountChanged();
            this.fetchBookings(this.selectedRequest.room.roomId).then(() => {
              this.cdr.detectChanges();
            });
          });
          if (modalElement) {
            const modal = window.bootstrap.Modal.getInstance(modalElement);
            if (modal) {
              modal.hide();
            }
          }
        },
        error: error => {
          this.toastNotif.showError(error.error);
        }
      })
      
    }
  }








  fetchBookings(roomId: number): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.token) {
        this.bookingService.getBookingsByRoomId(this.token, roomId).subscribe({
          next: (data) => {

            data = data.filter(booking => {
              const now = new Date();
              return new Date(booking.startTime) >= now;
            });


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



  getBackGroundColor(status: string){
    if(status=="ACCEPTED"){
      return "accepted";
    }
    else if(status=="REJECTED") {
      return "rejected"
    }
    return "on_hold";
  }

  getCleaningDuration(startDate: string, endDate:string){
    const date1 = new Date(startDate);
    const date2 = new Date(endDate);

    const diffInMs = date2.getTime() - date1.getTime();

    const diffInMinutes = Math.round(diffInMs / 60000); 
    return diffInMinutes;
  }

}
