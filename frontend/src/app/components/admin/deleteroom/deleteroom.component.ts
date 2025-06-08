import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { RoomService } from '../../../services/room.service';
import { RoomRequest } from '../../../model/class/Request/RoomRequest';
import { CommonModule } from '@angular/common';
import { ToastnotificationService } from '../../../services/toastnotification.service';

@Component({
  selector: 'app-deleteroom',
  imports: [CommonModule],
  templateUrl: './deleteroom.component.html',
  styleUrl: './deleteroom.component.css'
})
export class DeleteroomComponent implements OnInit{
  
  
  constructor(private cdr: ChangeDetectorRef){};
  
  roomService = inject(RoomService);
  toastNotif = inject(ToastnotificationService);

  rooms: RoomRequest[] = [];

  selectedRooms: RoomRequest[] = [];
  
  roomLocations: string[] = [];
  
  selectedRoom!: RoomRequest;

  activeTab: string = 'main';
  
  ngOnInit(): void {
    this.getAllRooms();
    
  }

  selectTab(tab: string){
    
    this.activeTab = tab;

    if(tab==='main'){
      this.selectedRooms = this.rooms.filter(room=> room.location==='Main Building');
    }
    else if(tab==='east'){
      this.selectedRooms = this.rooms.filter(room=> room.location==='East Wing');
    }
    else{
      this.selectedRooms = this.rooms.filter(room=> room.location==='West Wing');
    }
  }

  

  getAllRooms(){
    this.roomService.getAllRooms().subscribe({
      next: (data) => {
        // sort based on name
        data.sort((a, b) => a.name.localeCompare(b.name));
        this.rooms = data;
        console.log(this.rooms);
        this.selectedRooms = this.rooms.filter(room=> room.location==='Main Building');

          
        // Save locations
        for(let i=0; i<this.rooms.length; i++){
          let location = this.rooms[i].location;
          
          if(!this.roomLocations.includes(location)){
            this.roomLocations.push(location);
          }
        }
        
        this.roomLocations.sort((a, b) => {
          const order = ['Main', 'North', 'South', 'East', 'West'];
          return order.indexOf(a.split(' ')[0]) - order.indexOf(b.split(' ')[0]);
          
        });
        
        
      },
      error: (error) => console.error('Error fetching rooms:', error)
    });
  }


  showDeleteForm(room: RoomRequest){
    console.log(room);
    this.selectedRoom = room;
    const modal = new window.bootstrap.Modal(document.getElementById('deleteRoomModal'));
    modal.show();
  }


  deleteRoom(roomId: number){
    const token = localStorage.getItem('jwtToken');
    if(token){
      this.roomService.deleteRoom(token, roomId).subscribe({
        next: (data) =>{
          this.toastNotif.showSuccess(data.message);
          

          const modalElement = document.getElementById('deleteRoomModal');
          if (modalElement) {
            const modalInstance = window.bootstrap.Modal.getInstance(modalElement);
            if (modalInstance) {
              modalInstance.hide();
            }
          }
          this.getAllRooms();
          this.cdr.detectChanges();
        },
        error: (error)=> {
          const errorMessage = error?.error || "An unknown error occurred";
          this.toastNotif.showError(errorMessage)
        }
      })
    }
  }
}
