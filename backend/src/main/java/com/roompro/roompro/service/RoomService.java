package com.roompro.roompro.service;

import com.roompro.roompro.dto.request.NewRoomRequestDTO;
import com.roompro.roompro.dto.response.EquipmentResponseDTO;
import com.roompro.roompro.dto.response.RoomCleaningResponseDTO;
import com.roompro.roompro.dto.response.RoomResponseDTO;
import com.roompro.roompro.model.Equipment;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.repository.RoomRepository;
import com.roompro.roompro.service.mapper.RoomMapper;
import com.roompro.roompro.service.mapper.RoomMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    private RoomMapper roomMapper = new RoomMapperImpl();


    @Autowired
    EquipmentMappingService equipmentMappingService;


    public List<Room> getAllRooms() {
        return roomRepository.findAllWithEquipment();
    }


    public List<RoomCleaningResponseDTO> getAllRoomsWithCleaningType(){
        List<Object[]> rooms = roomRepository.findRoomsWithCleaningType();

        List<RoomCleaningResponseDTO> roomCleaningResponseDTOs = new ArrayList<>(); ;
        for (Object[] row : rooms) {
            Room room = (Room) row[0];
            RoomResponseDTO rr = roomMapper.roomToRoomResponseDTO(room);

            String cleaningType = (String) row[1];
            String cleaningDescription = (String) row[2];


            roomCleaningResponseDTOs.add(new RoomCleaningResponseDTO(rr, cleaningType, cleaningDescription));
        }
        return roomCleaningResponseDTOs;
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

    public void updateCleaningType(long roomId, long cleaningId){
        roomRepository.updateCleaningType(roomId, cleaningId);
    }
}

