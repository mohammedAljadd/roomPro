package com.roompro.roompro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MaintenanceResponseDTO {
    private long roomId;
    private String startDate;
    private String endDate;
}

