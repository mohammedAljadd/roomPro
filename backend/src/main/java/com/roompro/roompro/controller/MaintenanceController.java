package com.roompro.roompro.controller;


import com.roompro.roompro.dto.response.CleaningAfterUseResponseDTO;
import com.roompro.roompro.dto.response.MaintenanceResponseDTO;
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
    public List<MaintenanceResponseDTO> getAllCleaningAfterUse(
            @RequestParam(required = true) Long roomId){

        List<Maintenance> maintenances = maintenanceService.findMaintenanceSlotsByRoom(roomId);

        return maintenances.stream().map(m->new MaintenanceResponseDTO(m.getRoom().getRoomId(),
                m.getStartDate().toString(), m.getEndDate().toString())).toList();
    }


}
