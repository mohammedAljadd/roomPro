package com.roompro.roompro.dto.response;

import com.roompro.roompro.enums.CleaningStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CleaningOnRequestResponseDTO {
    private Long cleaningId;
    private RoomResponseDTO room;
    private LocalDateTime requestedAt;
    private CleaningStatus status;
    private String message;
    private String userFirstName;
    private String userLastName;
}

