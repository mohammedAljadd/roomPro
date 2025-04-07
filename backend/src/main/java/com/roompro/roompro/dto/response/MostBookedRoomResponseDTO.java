package com.roompro.roompro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MostBookedRoomResponseDTO {
    private String name;
    private Short capacity;
    private String location;
    private String description;
    private long booking_count;
}
