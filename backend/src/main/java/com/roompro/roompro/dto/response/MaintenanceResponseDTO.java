package com.roompro.roompro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MaintenanceResponseDTO {
    private long roomId;
    private String startTime;
    private String endTime;
    private String startDate;
    private String endDate;
}

