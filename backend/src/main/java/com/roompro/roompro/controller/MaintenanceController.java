package com.roompro.roompro.controller;


import com.roompro.roompro.dto.response.CleaningAfterUseResponseDTO;
import com.roompro.roompro.dto.response.MaintenanceResponseDTO;
import com.roompro.roompro.dto.response.RoomMaintenanceResponseDTO;
import com.roompro.roompro.model.CleaningAfterUse;
import com.roompro.roompro.model.Maintenance;
import com.roompro.roompro.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @DeleteMapping("/maintenance/delete/{maintenance_id}")
    public ResponseEntity<?> deleteMaintenance(@PathVariable Long maintenance_id) throws Exception {
        try{
            Map<String, String>  response = maintenanceService.deleteById(maintenance_id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/maintenance/add")
    public ResponseEntity<?> addMaintenance(@RequestBody MaintenanceResponseDTO maintenanceDTO){

        System.out.println(maintenanceDTO.toString());
        if (maintenanceDTO.getRoomId() == 0 ||
                maintenanceDTO.getStartDate() == null || maintenanceDTO.getStartDate().isEmpty() ||
                maintenanceDTO.getEndDate() == null || maintenanceDTO.getEndDate().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All fields are required.");
        }



        try{
            maintenanceService.createMaintenance(maintenanceDTO);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Maintenance slot was added successfully.");
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
