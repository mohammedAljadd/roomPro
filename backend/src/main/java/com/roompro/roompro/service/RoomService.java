package com.roompro.roompro.service;

import com.roompro.roompro.dto.request.NewRoomRequestDTO;
import com.roompro.roompro.dto.response.EquipmentResponseDTO;
import com.roompro.roompro.dto.response.RoomCleaningResponseDTO;
import com.roompro.roompro.dto.response.RoomResponseDTO;
import com.roompro.roompro.model.Booking;
import com.roompro.roompro.model.Equipment;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.repository.RoomRepository;
import com.roompro.roompro.service.mapper.RoomMapper;
import com.roompro.roompro.service.mapper.RoomMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    private RoomMapper roomMapper = new RoomMapperImpl();


    @Autowired
    EquipmentMappingService equipmentMappingService;

    @Autowired
    BookingService bookingService;

    @Autowired
    CleaningService cleaningService;

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

    public void updateCleaningType(long roomId, long cleaningId, long previousCleaningId){
        roomRepository.updateCleaningType(roomId, cleaningId);
        System.out.println("previousCleaningId : "+previousCleaningId);
        // Remove after use cleaning if no longer used
        if(previousCleaningId==1){
            System.out.println("roomId " +roomId);
            cleaningService.deleteAfterUseCleaningSlots(roomId);
        }



        // Add cleaning slots after future booking if no overlap
        // After use cleaning
        if(cleaningId==1){
            // for each room booking add cleaning slot if possible
            List<Booking> roomBookings = bookingService.getBookingsByRoomId(roomId);
            for(Booking booking: roomBookings){
                LocalDateTime cleaningStart = booking.getEndTime();
                LocalDateTime cleaningEnd = cleaningStart.plusMinutes(20); // 20 minutes of cleaning

                boolean hasOverlap = roomBookings.stream()
                        .anyMatch(otherBooking ->
                                cleaningStart.isBefore(otherBooking.getEndTime()) &&
                                        cleaningEnd.isAfter(otherBooking.getStartTime())
                        );

                if(!hasOverlap){
                    cleaningService.setCleaningSlot(cleaningStart, roomRepository.findById(roomId).get());
                }
            }
        }
    }
}

