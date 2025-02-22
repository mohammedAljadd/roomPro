package com.roompro.roompro.controller;


import com.roompro.roompro.dto.response.RoomResponseDTO;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.service.RoomService;
import com.roompro.roompro.service.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roompro")
@CrossOrigin(origins = "http://localhost:4209")
public class RoomController {

    @Autowired
    RoomService roomService;

    @Autowired
    RoomMapper roomMapper;


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
}
