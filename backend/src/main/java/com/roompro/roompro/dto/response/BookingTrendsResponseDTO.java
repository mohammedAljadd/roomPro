package com.roompro.roompro.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingTrendsResponseDTO {
    private int totalBookings;
    private String peakHour;
    private String peakDay;
    private MostBookedRoomResponseDTO mostBookedRoom;
    private double averageBookingDuration;

}
