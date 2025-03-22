package com.roompro.roompro.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EquipmentUpdateRequestDTO {
    private Long equipmentId;
    private Long roomId;
    private boolean previousValue;
    private boolean newValue;
}
