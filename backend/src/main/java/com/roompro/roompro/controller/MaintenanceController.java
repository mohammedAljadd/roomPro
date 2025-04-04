package com.roompro.roompro.controller;


import com.roompro.roompro.dto.response.CleaningAfterUseResponseDTO;
import com.roompro.roompro.dto.response.MaintenanceResponseDTO;
import com.roompro.roompro.dto.response.RoomMaintenanceResponseDTO;
import com.roompro.roompro.model.CleaningAfterUse;
import com.roompro.roompro.model.Maintenance;
import com.roompro.roompro.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roompro")
@CrossOrigin(origins = "http://localhost:4209")
public class MaintenanceController {

    @Autowired
    MaintenanceService maintenanceService;

    @GetMapping("/maintenance/get-slots")
    public List<MaintenanceResponseDTO> getMaintenanceSlots(
            @RequestParam(required = true) Long roomId){

        List<Maintenance> maintenances = maintenanceService.findMaintenanceSlotsByRoom(roomId);

        return maintenances.stream().map(m->new MaintenanceResponseDTO(m.getMaintenanceId(), m.getRoom().getRoomId(),
                m.getStartDate().toString(), m.getEndDate().toString())).toList();
    }

    @GetMapping("/maintenance/get-slots/all")
    public List<RoomMaintenanceResponseDTO> getAllMaintenanceSlots(){

        return maintenanceService.findAll();

    }


}
