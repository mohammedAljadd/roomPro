package com.roompro.roompro.service;

import com.roompro.roompro.model.Room;
import com.roompro.roompro.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> filterRooms(Integer capacity, String location, String equipment) {
        List<Room> rooms = roomRepository.findAll();

        // Filter capacity
        if (capacity != null) {
            rooms = rooms.stream()
                    .filter(room -> room.getCapacity() >= capacity)
                    .collect(Collectors.toList());
        }

        // Filter location (case-insensitive partial match)
        if (location != null && !location.isEmpty()) {
            String lowerLocation = location.toLowerCase();
            rooms = rooms.stream()
                    .filter(room -> room.getLocation().toLowerCase().contains(lowerLocation))
                    .collect(Collectors.toList());
        }

        // Filter equipment (case-insensitive and trim whitespace)
        if (equipment != null && !equipment.isEmpty()) {
            List<String> equipmentList = Arrays.stream(equipment.split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            // Check if room contains ALL selected equipment (AND logic)
            rooms = rooms.stream()
                    .filter(room -> {
                        Set<String> roomEquipment = room.getEquipment().stream()
                                .map(eq -> eq.getName().toLowerCase())
                                .collect(Collectors.toSet());
                        return roomEquipment.containsAll(equipmentList);
                    })
                    .collect(Collectors.toList());
        }

        return rooms;
    }
}

