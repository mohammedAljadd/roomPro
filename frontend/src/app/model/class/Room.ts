import { Booking } from "./Booking";
import { roomEquipmentMapping } from "./roomEquipmentMapping";
export interface Room {
    roomId: number;
    name: string;
    capacity: number;
    location: string;
    description: string;
    bookings: Booking[];
    roomEquipmentMappings: roomEquipmentMapping[];
  }
  


  