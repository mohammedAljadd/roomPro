import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { RoomRequest } from '../../../model/class/Request/RoomRequest';
import { RoomService } from '../../../services/room.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EquipementService } from '../../../services/equipement.service';
import { EquipementRequest } from '../../../model/class/Request/EquipementRequest';
import { EquipmentUpdateResponse } from '../../../model/class/Response/EquipmentUpdateResponse';
import { ToastnotificationService } from '../../../services/toastnotification.service';

@Component({
  selector: 'app-addremoveequipment',
  imports: [CommonModule, FormsModule],
  templateUrl: './addremoveequipment.component.html',
  styleUrl: './addremoveequipment.component.css'
})
export class AddRemoveEquipmentComponent implements OnInit{

  rooms: RoomRequest[] = [];
  filteredRooms: RoomRequest[] = [];
  chosenRoomName: string = "";
  roomNameInModal: string = "";
  allEquipments: EquipementRequest[] = [];
  
  roomService = inject(RoomService);
  equipmentService = inject(EquipementService);

  toastNotif = inject(ToastnotificationService);

  roomsEquipmentsMappingChange = new Map<number, { previousValue: boolean, newValue: boolean }>();

  equipmentUpdate:EquipmentUpdateResponse[] = [];

  chosenRoomId!: number;
  roomToUpdateName!:string;

  constructor(private cdr: ChangeDetectorRef){};

  ngOnInit(): void {
    this.getAllRooms();

  }

  getAllRooms(){
    this.roomService.getAllRooms().subscribe({
      next: (data) => {
        // sort based on capacity
        data.sort((a, b) => a.capacity - b.capacity);
        this.rooms = data;
        this.filteredRooms = data;
      },
      error: (error) => console.error('Error fetching rooms:', error)
    });
  }

  getChosenRoomName(){
    this.filterRoomByName(this.chosenRoomName);
  }

  filterRoomByName(roomeName: string){
    this.filteredRooms = this.rooms.filter(room => room.name.toLowerCase().startsWith(roomeName.toLowerCase()));
  }


  openEquipemetForm(roomId: number): void {
    const modal = new window.bootstrap.Modal(document.getElementById('equipmentModal'));
    modal.show();
    this.chosenRoomId = roomId;
    this.fetchAllEquipements(roomId); // fetch all equipements
    this.roomsEquipmentsMappingChange.clear();
    this.roomNameInModal = this.getRoomName();
  }


  getRoomName(): string{
    let roomname = "";
    for(const room of this.rooms){
      if(room.roomId==this.chosenRoomId){
        roomname = room.name;
        break;
      }
    }
    return roomname;
  }



  fetchAllEquipements(roomId: number){
    const token = localStorage.getItem('jwtToken');

    if (token) {
    this.equipmentService.fetAllEquipements(token, roomId).subscribe({
      next: (data) => {
        this.allEquipments = data;
      },
      error: (error) => console.error('Error fetching rooms:', error)
    });
    }
  }

  toggleAvailability(equipment: EquipementRequest, event: any){
    const previousValue = equipment.isAvailable;
    const newValue = event.target.checked;
    
    
    if(previousValue != newValue){
      this.roomsEquipmentsMappingChange.set(equipment.equipmentId, { previousValue, newValue });
    }
    else if(this.roomsEquipmentsMappingChange.has(equipment.equipmentId)){
      this.roomsEquipmentsMappingChange.delete(equipment.equipmentId);
    }
  }


  updateEquipements(){
    
    this.equipmentUpdate = Array.from(this.roomsEquipmentsMappingChange, ([equipmentId, values]) => ({
      equipmentId: equipmentId,
      roomId: this.chosenRoomId,
      previousValue: values.previousValue,
      newValue: values.newValue
    }));

  
    
    const token = localStorage.getItem('jwtToken');
    if(token){
      this.equipmentService.updateEquipements(token, this.equipmentUpdate).subscribe(
        response => {
          this.getAllRooms();
          this.roomsEquipmentsMappingChange.clear();
          this.toastNotif.showSuccess(response.message, 'Update received');
          this.cdr.detectChanges();
          
        },
        error => {
          console.log("Failed");
        }
      );
      
    }

    document.body.focus();

    const modalElement = document.getElementById('equipmentModal');
    if (modalElement) {
      const modal = window.bootstrap.Modal.getInstance(modalElement);
      if (modal) {
        modal.hide();
      }
    }
    
  }

  
}
