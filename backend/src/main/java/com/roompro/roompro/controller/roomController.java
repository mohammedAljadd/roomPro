package com.roompro.roompro.controller;


import com.roompro.roompro.dto.BookingDto;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.service.BookingService;
import com.roompro.roompro.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roompro")
@CrossOrigin(origins = "http://localhost:4209")
public class roomController {

    @Autowired
    RoomService roomService;

    @GetMapping("/meeting-rooms")
    public List<Room> getRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return rooms;
    }

    @GetMapping("/meeting-rooms/filter")
    public List<Room> filterRooms(
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String equipment) {
        System.out.println(equipment + location + equipment);
        return roomService.filterRooms(capacity, location, equipment);
    }
}
