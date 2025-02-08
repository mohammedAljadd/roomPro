import { Component, OnInit } from '@angular/core';
import { Room } from '../../model/class/Room';
import { RoomService } from '../../services/room.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-meetingrooms',
  standalone: true,
  imports: [CommonModule, FormsModule],  // 'standalone: true' for Angular 15+
  templateUrl: './meetingrooms.component.html',
  styleUrls: ['./meetingrooms.component.css']  // Fixed typo here 'styleUrls'
})
export class MeetingroomsComponent implements OnInit {
  rooms: Room[] = []; 
  filteredRooms: Room[] = []; 
  selectedCapacity: number = 0; 
  selectedLocation: string = ''; 
  selectedEquipment: string[] = [];  // equipment should be an array of selected equipment names

  uniqueLocations: string[] = []; 
  uniqueEquipment: string[] = []; 

  constructor(private roomService: RoomService) {}

  ngOnInit(): void {
    this.roomService.getAllRooms().subscribe({
      next: (data) => {
        this.rooms = data;
        this.filteredRooms = data;
        this.uniqueLocations = [...new Set(data.map(room => room.location))];
        this.uniqueEquipment = [
          ...new Set(data.flatMap(room => 
            room.roomEquipmentMappings.flatMap(mapping => 
              mapping.equipment.name
            )
          ))
        ];
        console.log(this.rooms);
      },
      error: (error) => console.error('Error fetching rooms:', error)
    });
  }

  applyFilters(): void {
    this.getFilteredRooms(); 
  }

  getFilteredRooms(): void {
    // Convert selectedEquipment array into a comma-separated string
    const equipmentFilter = this.selectedEquipment.join(',');
  
    // Now pass it as a string to the API
    this.roomService.getFilteredRooms(this.selectedCapacity, this.selectedLocation, equipmentFilter).subscribe({
      next: (data) => {
        this.filteredRooms = data; // store filtered rooms in filteredRooms array
        console.log(this.filteredRooms);
      },
      error: (error) => console.error('Error fetching filtered rooms:', error)
    });
  }
  
}
