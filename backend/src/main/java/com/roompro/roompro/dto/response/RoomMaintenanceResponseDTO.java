package com.roompro.roompro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomMaintenanceResponseDTO {
    RoomResponseDTO roomDetails;
    MaintenanceResponseDTO maintenances;
}
