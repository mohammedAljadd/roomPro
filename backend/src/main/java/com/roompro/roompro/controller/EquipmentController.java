package com.roompro.roompro.controller;


import com.roompro.roompro.dto.response.EquipmentResponseDTO;
import com.roompro.roompro.model.Equipment;
import com.roompro.roompro.service.EquipmentService;
import com.roompro.roompro.service.mapper.EquipmentMapper;
import com.roompro.roompro.service.mapper.EquipmentMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roompro")
@CrossOrigin(origins = "http://localhost:4209")
public class EquipmentController {


    @Autowired
    EquipmentService equipmentService;

    private EquipmentMapper equipmentMapper = new EquipmentMapperImpl();


    @GetMapping("/equipments")
    public List<EquipmentResponseDTO> getEquipments(@RequestParam(required = false) Long roomId) {
        return equipmentService.getAllEquipments(roomId);
    }

}
