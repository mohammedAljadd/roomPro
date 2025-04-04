package com.roompro.roompro.service;


import com.roompro.roompro.model.Maintenance;
import com.roompro.roompro.repository.MaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaintenanceService {

    @Autowired
    MaintenanceRepository maintenanceRepository;

    public List<Maintenance> findMaintenanceSlotsByRoom(Long roomId){
        return maintenanceRepository.findByRoom_RoomId(roomId);
    }

}
