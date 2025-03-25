package com.roompro.roompro.controller;


import com.roompro.roompro.dto.request.NewRoomRequestDTO;
import com.roompro.roompro.dto.response.RoomResponseDTO;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.service.RoomService;
import com.roompro.roompro.service.mapper.RoomMapper;
import com.roompro.roompro.service.mapper.RoomMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roompro")
@CrossOrigin(origins = "http://localhost:4209")
public class RoomController {

    @Autowired
    RoomService roomService;

    private RoomMapper roomMapper = new RoomMapperImpl();


    @GetMapping("/meeting-rooms")
    public List<RoomResponseDTO> getRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return rooms.stream()
                .map(roomMapper::roomToRoomResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/meeting-rooms/filter")
    public List<RoomResponseDTO> filterRooms(
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String equipmentNames) {


        if(capacity==null && location == null && equipmentNames == null){
            List<Room> rooms =  roomService.getAllRooms();
            return rooms.stream()
                    .map(roomMapper::roomToRoomResponseDTO)
                    .collect(Collectors.toList());
        }
        List<String> equipmentList;

        if(equipmentNames==null){
            equipmentList = null;
        }
        else if(equipmentNames.contains(",")){
            equipmentList = Arrays.asList(equipmentNames.split(","));
        }
        else{
            equipmentList = Arrays.asList(equipmentNames);
        }

        List<Room> rooms = roomService.getFilteredRooms(capacity, location, equipmentList);
        return rooms.stream()
                .map(roomMapper::roomToRoomResponseDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/add-meeting-rooms")
    public ResponseEntity<?> addMeetingRoom(@RequestBody NewRoomRequestDTO newRoomRequestDTO) {
        if (newRoomRequestDTO.getRoomName() == null || newRoomRequestDTO.getRoomName().isEmpty() ||
                newRoomRequestDTO.getCapacity() == 0 ||
                newRoomRequestDTO.getDescription() == null || newRoomRequestDTO.getDescription().isEmpty() ||
                newRoomRequestDTO.getLocation() == null || newRoomRequestDTO.getLocation().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All fields are required.");
        }

        roomService.saveRoom(newRoomRequestDTO);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Room was successfully added");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/meeting-rooms/delete/{room_id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long room_id) {

        try{
            Map<String, String>  response = roomService.deleteRoom(room_id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}
