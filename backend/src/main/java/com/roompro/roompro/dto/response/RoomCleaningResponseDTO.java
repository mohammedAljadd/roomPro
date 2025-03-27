package com.roompro.roompro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomCleaningResponseDTO {
    RoomResponseDTO roomDetails;
    private String cleaningType;
    private String cleaningDescription;
}
