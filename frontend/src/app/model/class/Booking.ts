export interface Booking {
    roomId: number;
    startTime: string; // Using string because JSON dates come as ISO strings
    hours: number;
  }