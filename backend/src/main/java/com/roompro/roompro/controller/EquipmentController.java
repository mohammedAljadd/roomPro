package com.roompro.roompro.controller;


import com.roompro.roompro.dto.request.EquipmentUpdateRequestDTO;
import com.roompro.roompro.dto.response.EquipmentResponseDTO;
import com.roompro.roompro.service.EquipmentMappingService;
import com.roompro.roompro.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.*;

@RestController
@RequestMapping("/roompro")
@CrossOrigin(origins = "http://localhost:4209")
public class EquipmentController {


    @Autowired
    EquipmentService equipmentService;

    @Autowired
    EquipmentMappingService equipmentMappingService;


    @GetMapping("/equipments")
    public List<EquipmentResponseDTO> getEquipments(@RequestParam(required = false) Long roomId) {
        return equipmentService.getAllEquipments(roomId);
    }


    @PostMapping("/equipments-update")
    public ResponseEntity<?> updateEquipmentMapping(@RequestBody EquipmentUpdateRequestDTO[] equipmentUpdateRequestDTO){

        ArrayList<Long[]> equipmentsRemoved = new ArrayList<>();
        ArrayList<Long[]> equipmentsAdded = new ArrayList<>();

        EquipmentUpdateRequestDTO mapping;
        for(int i=0; i<equipmentUpdateRequestDTO.length; i++){
            mapping = equipmentUpdateRequestDTO[i];

            if(mapping.isPreviousValue()){
                equipmentsRemoved.add(new Long[] {mapping.getEquipmentId(), mapping.getRoomId()});
            }
            else{
                equipmentsAdded.add(new Long[] {mapping.getEquipmentId(), mapping.getRoomId()});
            }
        }
       
        equipmentMappingService.updateRoomMapping(equipmentsAdded, equipmentsRemoved);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Update received");

        return ResponseEntity.ok(response);

    }
}
