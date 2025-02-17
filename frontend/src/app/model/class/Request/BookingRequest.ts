import { RoomRequest } from "./RoomRequest";

export interface BookingRequest {
    bookingId: number;
    room: RoomRequest;
    startTime: string;
    endTime: string;
    userEmail: string;
  }