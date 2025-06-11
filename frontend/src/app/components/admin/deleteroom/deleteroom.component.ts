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

  activeTab: string = '';
  
  ngOnInit(): void {
    this.getAllRooms();
    
  }

  private getLocationPriority(location: string): number {
    const firstWord = location.split(' ')[0];
    const order = ['Main', 'North', 'South', 'East', 'West'];
    const index = order.indexOf(firstWord);
    return index === -1 ? Number.MAX_SAFE_INTEGER : index;
  }

  selectTab(location: string){
    this.activeTab = location;
    this.selectedRooms = this.rooms.filter(room => room.location === location);
  }

  

  getAllRooms(){
    this.roomService.getAllRooms().subscribe({
      next: (data) => {
        this.rooms = data.sort((a, b) => a.name.localeCompare(b.name));
        this.roomLocations = [...new Set(this.rooms.map(room => room.location))];
        this.roomLocations.sort((a, b) => {
          const byPriority = this.getLocationPriority(a) - this.getLocationPriority(b);
          if (byPriority !== 0) return byPriority;
          return a.localeCompare(b);
        });

        if (this.roomLocations.length > 0) {
          const defaultLocation = this.roomLocations.includes(this.activeTab)
            ? this.activeTab
            : this.roomLocations[0];
          this.selectTab(defaultLocation);
        } else {
          this.activeTab = '';
          this.selectedRooms = [];
        }
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
