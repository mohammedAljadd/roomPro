package com.roompro.roompro.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EquipmentResponseDTO {
    private Long  equipmentId;
    private String name;
    @JsonProperty("isAvailable")
    private boolean isAvailable;
}
