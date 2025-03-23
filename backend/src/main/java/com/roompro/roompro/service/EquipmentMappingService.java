package com.roompro.roompro.service;

import com.roompro.roompro.model.Equipment;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.model.RoomEquipmentMapping;
import com.roompro.roompro.repository.EquipmentMappingRepository;
import com.roompro.roompro.repository.EquipmentRepository;
import com.roompro.roompro.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class EquipmentMappingService {

    @Autowired
    EquipmentMappingRepository equipmentMappingRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    EquipmentRepository equipmentRepository;

    public void updateRoomMapping(ArrayList<Long[]> equipmentsAdded, ArrayList<Long[]> equipmentsRemoved) {
        List<RoomEquipmentMapping> mappingToAdd = new ArrayList<>();
        List<RoomEquipmentMapping> mappingToRemove = new ArrayList<>();

        Long[] mapping;
        for (Long[] longs : equipmentsAdded) {

            mapping = longs;
            
            // Check if user resend the mapping multiple times
            if (!equipmentMappingRepository.findByRoomIdAndEquipmentId(mapping[0], mapping[1]).isEmpty()) {
                continue;
            }


            Optional<Room> room = roomRepository.findById(mapping[1]);
            Optional<Equipment> equipment = equipmentRepository.findById(mapping[0]);


            RoomEquipmentMapping roomEquipmentMapping = new RoomEquipmentMapping();
            roomEquipmentMapping.setEquipment(equipment.get());
            roomEquipmentMapping.setRoom(room.get());
            mappingToAdd.add(roomEquipmentMapping);
        }

        for (Long[] longs : equipmentsRemoved) {

            mapping = longs;


            List<RoomEquipmentMapping> equipmentMappings = equipmentMappingRepository.findByRoomIdAndEquipmentId(mapping[0], mapping[1]);

            mappingToRemove.add(equipmentMappings.getFirst());
        }

        equipmentMappingRepository.saveAll(mappingToAdd);
        equipmentMappingRepository.deleteAll(mappingToRemove);

    }
}
