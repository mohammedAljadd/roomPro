import { BookingRequest } from "./BookingRequest";

export interface RoomRequest {
    roomId: number;
    name: string;
    capacity: number;
    location: string;
    description: string;
    bookings: BookingRequest[];
    equipments: string[];
  }
  


  