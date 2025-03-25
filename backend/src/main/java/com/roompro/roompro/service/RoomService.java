package com.roompro.roompro.service;

import com.roompro.roompro.dto.request.NewRoomRequestDTO;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;


    @Autowired
    EquipmentMappingService equipmentMappingService;


    public List<Room> getAllRooms() {
        return roomRepository.findAllWithEquipment();
    }


    public List<Room> getFilteredRooms(Integer capacity, String location, List<String>equipmentNames) {
        return roomRepository.findAllWithFilters(capacity, location, equipmentNames);
    }

    public void saveRoom(NewRoomRequestDTO newRoom){

        // Save room in DB
        Room room = new Room();
        room.setName(newRoom.getRoomName());
        room.setLocation(newRoom.getLocation());
        room.setCapacity(newRoom.getCapacity());
        room.setDescription(newRoom.getDescription());
        List<Long> equipmentsIDs = newRoom.getEquipmentsIDs();

        Room savedRoom = roomRepository.save(room);

        // Add equipments
        equipmentMappingService.addEquipmentsToRoom(savedRoom.getRoomId(), equipmentsIDs);
    }

    public Map<String, String> deleteRoom(Long id) throws Exception {

        if(roomRepository.findById(id).isEmpty()){
            throw new Exception("Room not found.");
        }

        roomRepository.deleteById(id);
        return Map.of("message", "Room deleted successfully.");

    }
}

