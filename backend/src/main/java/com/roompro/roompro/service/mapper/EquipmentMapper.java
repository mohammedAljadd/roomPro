package com.roompro.roompro.service.mapper;


import com.roompro.roompro.dto.response.EquipmentResponseDTO;
import com.roompro.roompro.model.Equipment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {
    EquipmentResponseDTO equipmentToEquipmentResponseDTO(Equipment equipment);
}
