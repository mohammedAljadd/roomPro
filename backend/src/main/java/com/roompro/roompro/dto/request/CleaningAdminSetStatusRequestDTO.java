package com.roompro.roompro.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CleaningAdminSetStatusRequestDTO {
    private long cleaningId;
    private String status;
    private String startTime;
    private String endTime;
}
