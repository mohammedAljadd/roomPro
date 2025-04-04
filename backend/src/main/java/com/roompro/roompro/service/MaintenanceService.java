package com.roompro.roompro.service;


import com.roompro.roompro.dto.response.MaintenanceResponseDTO;
import com.roompro.roompro.dto.response.RoomCleaningResponseDTO;
import com.roompro.roompro.dto.response.RoomMaintenanceResponseDTO;
import com.roompro.roompro.dto.response.RoomResponseDTO;
import com.roompro.roompro.model.Maintenance;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.repository.MaintenanceRepository;
import com.roompro.roompro.repository.RoomRepository;
import com.roompro.roompro.service.mapper.RoomMapper;
import com.roompro.roompro.service.mapper.RoomMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MaintenanceService {

    @Autowired
    MaintenanceRepository maintenanceRepository;

    @Autowired
    RoomRepository roomRepository;

    private RoomMapper roomMapper = new RoomMapperImpl();

    public List<Maintenance> findMaintenanceSlotsByRoom(Long roomId){
        return maintenanceRepository.findByRoom_RoomId(roomId);
    }

    public List<RoomMaintenanceResponseDTO> findAll(){

        List<Object []> rms = roomRepository.findRoomsWithMaintenance();
        List<RoomMaintenanceResponseDTO> roomMaintenanceResponseDTOS = new ArrayList<>(); ;
        for (Object[] row : rms) {
            Room room = (Room) row[0];
            RoomResponseDTO rr = roomMapper.roomToRoomResponseDTO(room);

            Maintenance m = (Maintenance) row[1];



            RoomMaintenanceResponseDTO rm = new RoomMaintenanceResponseDTO();
            rm.setRoomDetails(rr);
            if(m!=null){
                rm.setMaintenances(new MaintenanceResponseDTO(m.getMaintenanceId(), m.getRoom().getRoomId(),
                        m.getStartDate().toString(), m.getEndDate().toString()));
            }
            else{
                rm.setMaintenances(null);
            }

            roomMaintenanceResponseDTOS.add(rm);
        }
        return roomMaintenanceResponseDTOS;
    }




    boolean isOverlappingWithMaintenanceSlots(long roomId, LocalDateTime startDate, LocalDateTime endDate){
        return maintenanceRepository.isOverlappingWithMaintenanceSlots(roomId, startDate, endDate);
    }



}
