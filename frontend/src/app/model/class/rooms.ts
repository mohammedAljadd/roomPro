import { Booking } from "./Booking";
import { RoomEquipment } from "./RoomEquipment";
export interface Room {
    roomId: number;
    name: string;
    capacity: number;
    location: string;
    description: string;
    bookings: Booking[];
    equipment: RoomEquipment[];
  }
  


  