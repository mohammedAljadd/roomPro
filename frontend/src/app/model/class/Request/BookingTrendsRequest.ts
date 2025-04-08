import { MostBookedRoomRequest } from "./MostBookedRoomRequest";

export class BookingTrendsRequest{
    totalBookings!: number;
    peakHour!: string;
    peakDay!: string;
    mostBookedRoom!: MostBookedRoomRequest;
    averageBookingDuration!: number;
}