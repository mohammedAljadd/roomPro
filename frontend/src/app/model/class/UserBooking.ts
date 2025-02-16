import { Room } from "./Room";
import { User } from "./User";

export interface UserBooking {
    bookingId: number;
    room: Room; // Using string because JSON dates come as ISO strings
    startTime: string;
    endTime: string;
    user: User;
  }