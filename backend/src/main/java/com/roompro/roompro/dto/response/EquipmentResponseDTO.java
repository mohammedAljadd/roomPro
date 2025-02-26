package com.roompro.roompro.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EquipmentResponseDTO {
    private Long  equipmentId;
    private String name;
    private boolean isAvailable;
}
