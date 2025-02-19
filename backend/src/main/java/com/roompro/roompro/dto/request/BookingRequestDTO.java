package com.roompro.roompro.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class BookingRequestDTO {
    private Long roomId;
    private String startTime;
    private  double bookingHours;
}
