package com.roompro.roompro.service;


import com.roompro.roompro.dto.response.EquipmentResponseDTO;
import com.roompro.roompro.model.Equipment;
import com.roompro.roompro.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    public List<EquipmentResponseDTO> getAllEquipments(Long roomId) {
        List<Object[]> equipments=  equipmentRepository.findAllEquipments(roomId);


        // Map the result to EquipmentResponseDTO
        List<EquipmentResponseDTO> equipmentResponseDTOs = new ArrayList<>();
        for (Object[] row : equipments) {
            Equipment equipment = (Equipment) row[0];
            Boolean isAvailable = (Boolean) row[1];

            // Create EquipmentResponseDTO and add it to the list
            EquipmentResponseDTO dto = new EquipmentResponseDTO(
                    equipment.getEquipmentId(),
                    equipment.getName(),
                    isAvailable != null ? isAvailable : false
            );
            equipmentResponseDTOs.add(dto);
        }
        return equipmentResponseDTOs;
    }

}
