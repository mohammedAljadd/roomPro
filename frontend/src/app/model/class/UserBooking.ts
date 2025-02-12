import { Room } from "./Room";

export interface UserBooking {
    bookingId: number;
    room: Room; // Using string because JSON dates come as ISO strings
    startTime: string;
    endTime: string
  }