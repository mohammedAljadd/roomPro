package com.roompro.roompro.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class BookingResponseDTO {
    private Long bookingId;
    private RoomResponseDTO room;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String userEmail;
    private boolean isCanceled;
}
