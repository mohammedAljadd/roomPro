package com.roompro.roompro.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long roomId;
    private String startTime;
    private  int hours;
}
