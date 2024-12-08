package com.roompro.roompro.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long roomId;
    private LocalDateTime startTime;
    private  LocalDateTime endTime;
}
