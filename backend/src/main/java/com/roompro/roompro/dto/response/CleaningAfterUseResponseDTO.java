package com.roompro.roompro.dto.response;

import com.roompro.roompro.model.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CleaningAfterUseResponseDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
