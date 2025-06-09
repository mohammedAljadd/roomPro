import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { RoomCleaningRequest } from '../../../model/class/Request/RoomCleaningRequest';
import { RoomService } from '../../../services/room.service';
import { CommonModule } from '@angular/common';
import { RoomSetCleaningResponse } from '../../../model/class/Response/RoomSetCleaningResponse';
import { ToastnotificationService } from '../../../services/toastnotification.service';

@Component({
  selector: 'app-cleaningperiods',
  imports: [CommonModule],
  templateUrl: './cleaningperiods.component.html',
  styleUrl: './cleaningperiods.component.css'
})
export class CleaningperiodsComponent implements OnInit{


  constructor(private cdr: ChangeDetectorRef){};

  rooms: RoomCleaningRequest[] = [];

  selectedRooms:RoomCleaningRequest[] = [];


  cleaningTypes: string[] = ['After Each Use', 'Weekly Cleaning - Friday', 'Weekly Cleaning - Wednesday', 'Custom Cleaning'];

  selectedRoom!: RoomCleaningRequest;

  roomService = inject(RoomService);

  newCleaningTypeId!: number;
  previousCleaningTypeId!: number;
  token!: string | null;
  
  toastNotif = inject(ToastnotificationService);

  activeTab:string = 'eachuse';

  ngOnInit(): void {
    this.token = localStorage.getItem('jwtToken');
    this.getAllRooms();
    
  }

  selectTab(tab: string){
    
    this.activeTab = tab;

    if(tab==='eachuse'){
      this.selectedRooms = this.rooms.filter(room=>room.cleaningType==='After Each Use');
          
    }

    else if(tab==='eachfriday'){
      this.selectedRooms = this.rooms.filter(room=> room.cleaningType==='Weekly Cleaning - Friday');
    }
    else if(tab==='eachwednesday'){
      this.selectedRooms = this.rooms.filter(room=> room.cleaningType==='Weekly Cleaning - Wednesday');
    }
    else{
      
      this.selectedRooms = this.rooms.filter(room=> room.cleaningType==='Custom Cleaning');
    }
  }

  getAllRooms(){
    
    if(this.token){
      this.roomService.getRoomsWithCleaningType(this.token).subscribe({
        next: (data)=>{
          this.rooms = data;
          this.selectedRooms = this.rooms.filter(room=>room.cleaningType==='After Each Use');
        },
        error: (error)=>{
          console.log("error " + error.error);
        }
      })
    }
  }

  showCleaningModal(room: RoomCleaningRequest){
    this.selectedRoom = room;
    const modal = new window.bootstrap.Modal(document.getElementById('cleaningTypeModal'));
    modal.show();
  }


  setCleaningModal(room: RoomCleaningRequest, event: any){
    // get cleaningtype
    const cleaningType = event.target.value;
    let cleaningTypesIDs = ['After Each Use', 'Weekly Cleaning - Friday', 'Weekly Cleaning - Wednesday', 'Custom Cleaning'];

    this.newCleaningTypeId = cleaningTypesIDs.indexOf(cleaningType)+1;
    this.previousCleaningTypeId = cleaningTypesIDs.indexOf(this.selectedRoom.cleaningType)+1; 

  }


  setit(){
    const token = localStorage.getItem('jwtToken');
    if(token){
      this.roomService.setCleaningType(this.selectedRoom.roomDetails.roomId, this.newCleaningTypeId, this.previousCleaningTypeId, token).subscribe({
        next: (data)=>{
          
          this.toastNotif.showSuccess(data.message);
          const modalElement = document.getElementById('cleaningTypeModal');
          if (modalElement) {
            const modalInstance = window.bootstrap.Modal.getInstance(modalElement);
            if (modalInstance) {
              modalInstance.hide();
            }
          }

          this.getAllRooms();
          
          this.cdr.detectChanges();
        },
        error: (error)=>{
          const errorMessage = error?.error || "An unknown error occurred";
          this.toastNotif.showError(errorMessage)
        }
      
      })
    }
  }



}
