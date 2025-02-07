export interface Booking {
    bookingId: number;
    startTime: string; // Using string because JSON dates come as ISO strings
    endTime: string;
  }