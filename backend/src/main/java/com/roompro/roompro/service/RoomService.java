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
        return roomRepository.findAllWithEquipment();
    }


    public List<Room> getFilteredRooms(Integer capacity, String location, List<String>equipmentNames) {
        return roomRepository.findAllWithFilters(capacity, location, equipmentNames);
    }
}

