package com.roompro.roompro.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.roompro.roompro.config.TestSecurityConfig;
import com.roompro.roompro.dto.response.RoomResponseDTO;
import com.roompro.roompro.model.Equipment;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.model.RoomEquipmentMapping;
import com.roompro.roompro.service.JWTService;
import com.roompro.roompro.service.RoomService;
import com.roompro.roompro.service.mapper.RoomMapper;
import com.roompro.roompro.service.mapper.RoomMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(RoomController.class)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class RoomControllerTest {


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoomService roomService;

    @MockitoBean
    private JWTService jwtService;

    private List<Room> mockedRooms;


    private RoomMapper roomMapper = new RoomMapperImpl();


    @BeforeEach
    void setUp(){
        mockedRooms = new ArrayList<>();
        mockedRooms.add(new Room(1L, "Room 1", (short) 5, "5th floor", "Small room", Collections.emptyList(), Collections.emptyList()));
        mockedRooms.add(new Room(12L, "Room 2", (short) 7, "1th floor", "Small room", Collections.emptyList(), Collections.emptyList()));
        mockedRooms.add(new Room(41L, "Hall 1", (short) 50, "Ground floor", "Large Hall", Collections.emptyList(), Collections.emptyList()));
        mockedRooms.add(new Room(91L, "Hall 3", (short) 60, "Ground floor", "Large Hall", Collections.emptyList(), Collections.emptyList()));
        mockedRooms.add(new Room(14L, "Training room 1", (short) 120, "5th floor", "Very large training room", Collections.emptyList(), Collections.emptyList()));
        mockedRooms.add(new Room(25L, "Training room 2", (short) 150, "5th floor", "Very large training room", Collections.emptyList(), Collections.emptyList()));


        // Adding equipements
        mockedRooms.get(0).setRoomEquipmentMappings(Arrays.asList(
                new RoomEquipmentMapping(1L, mockedRooms.get(0), new Equipment(1L, "Projector", null, null, null))
        ));

        mockedRooms.get(1).setRoomEquipmentMappings(Arrays.asList(
                new RoomEquipmentMapping(1L, mockedRooms.get(1), new Equipment(2L, "Whiteboard", null, null, null)),
                new RoomEquipmentMapping(1L, mockedRooms.get(1), new Equipment(3L, "Conference Phone", null, null, null))
        ));

        mockedRooms.get(2).setRoomEquipmentMappings(Arrays.asList(
                new RoomEquipmentMapping(1L, mockedRooms.get(2), new Equipment(4L, "Sound System", null, null, null)),
                new RoomEquipmentMapping(2L, mockedRooms.get(2), new Equipment(5L, "Stage Lighting", null, null, null))
        ));

        mockedRooms.get(3).setRoomEquipmentMappings(Arrays.asList(
                new RoomEquipmentMapping(3L, mockedRooms.get(3), new Equipment(6L, "Microphones", null, null, null)),
                new RoomEquipmentMapping(4L, mockedRooms.get(3), new Equipment(7L, "Flipchart", null, null, null))
        ));

        mockedRooms.get(4).setRoomEquipmentMappings(Arrays.asList(
                new RoomEquipmentMapping(5L, mockedRooms.get(4), new Equipment(8L, "Computer", null, null, null)),
                new RoomEquipmentMapping(6L, mockedRooms.get(4), new Equipment(9L, "Video Conferencing", null, null, null))
        ));

        mockedRooms.get(5).setRoomEquipmentMappings(Arrays.asList(
                new RoomEquipmentMapping(7L, mockedRooms.get(5), new Equipment(1L, "Projector", null, null, null)),
                new RoomEquipmentMapping(8L, mockedRooms.get(5), new Equipment(3L, "Conference Phone", null, null, null)),
                new RoomEquipmentMapping(9L, mockedRooms.get(5), new Equipment(6L, "Microphones", null, null, null))
        ));



    }

    @Test
    void shouldReturnAllMeetingRooms() throws Exception  {
        List<RoomResponseDTO> mockedRoomDTO = mockedRooms.stream()
                .map(roomMapper::roomToRoomResponseDTO)
                .collect(Collectors.toList());

        String expectedJson = objectMapper.writeValueAsString(mockedRoomDTO);

        when(roomService.getAllRooms()).thenReturn(mockedRooms);

        mockMvc.perform(get("/roompro/meeting-rooms"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }


    @Test
    void shouldReturnAllMeetingRoomsWhenNoFilterIsProvided() throws Exception {
        List<RoomResponseDTO> mockedRoomDTO = mockedRooms.stream()
                .map(roomMapper::roomToRoomResponseDTO)
                .collect(Collectors.toList());

        String expectedJson = objectMapper.writeValueAsString(mockedRoomDTO);

        when(roomService.getAllRooms()).thenReturn(mockedRooms);
        mockMvc.perform(get("/roompro/meeting-rooms/filter"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldFilterRoomsByCapacity() throws Exception{

        int capacity = 40;
        String location = null;
        List<String> equipementNames = null;

        mockedRooms = mockedRooms.stream().filter(room -> room.getCapacity() >capacity).collect(Collectors.toList());

        List<RoomResponseDTO> mockedRoomDTO = mockedRooms.stream()
                .map(roomMapper::roomToRoomResponseDTO)
                .collect(Collectors.toList());

        String expectedJson = objectMapper.writeValueAsString(mockedRoomDTO);

        when(roomService.getFilteredRooms(capacity, location, equipementNames)).thenReturn(mockedRooms);
        mockMvc.perform(get("/roompro/meeting-rooms/filter")
                        .param("capacity", String.valueOf(capacity)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldFilterRoomsByLocation() throws Exception {


        String location = "1st floor";
        List<String> equipementNames = null;

        mockedRooms = mockedRooms.stream().filter(room -> room.getLocation() == location).collect(Collectors.toList());

        List<RoomResponseDTO> mockedRoomDTO = mockedRooms.stream()
                .map(roomMapper::roomToRoomResponseDTO)
                .collect(Collectors.toList());

        String expectedJson = objectMapper.writeValueAsString(mockedRoomDTO);

        when(roomService.getFilteredRooms(null, location, equipementNames)).thenReturn(mockedRooms);
        mockMvc.perform(get("/roompro/meeting-rooms/filter")
                        .param("location", location))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

    }

    @Test
    void shouldFilterRoomsByEquipment() throws Exception{

        List<String> equipementNames = Arrays.asList("Projector", "Sound system");

        mockedRooms = mockedRooms.stream()
                .filter(room -> room.getRoomEquipmentMappings().stream()
                        .anyMatch(eq -> equipementNames.contains(eq.getEquipment().getName())))
                .collect(Collectors.toList());


        List<RoomResponseDTO> mockedRoomDTO = mockedRooms.stream()
                .map(roomMapper::roomToRoomResponseDTO)
                .collect(Collectors.toList());

        String expectedJson = objectMapper.writeValueAsString(mockedRoomDTO);

        when(roomService.getFilteredRooms(null, null, equipementNames)).thenReturn(mockedRooms);
        mockMvc.perform(get("/roompro/meeting-rooms/filter")
                        .param("equipmentNames", String.join(",", equipementNames)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldFilterRoomsByMultipleCriteria() throws Exception {
        Integer capacity = 50;
        String location = "5th floor";
        List<String> equipmentNames = Arrays.asList("Projector", "Sound System");


        List<Room> filteredRooms = mockedRooms.stream()
                .filter(room -> room.getCapacity() >= capacity)
                .filter(room -> room.getLocation().equals(location))
                .filter(room -> room.getRoomEquipmentMappings().stream()
                        .anyMatch(eq -> equipmentNames.contains(eq.getEquipment().getName())))
                .collect(Collectors.toList());

        List<RoomResponseDTO> expectedRoomDTOs = filteredRooms.stream()
                .map(roomMapper::roomToRoomResponseDTO)
                .collect(Collectors.toList());

        String expectedJson = objectMapper.writeValueAsString(expectedRoomDTOs);

        when(roomService.getFilteredRooms(capacity, location, equipmentNames)).thenReturn(filteredRooms);

        mockMvc.perform(get("/roompro/meeting-rooms/filter")
                        .param("capacity", String.valueOf(capacity))
                        .param("location", location)
                        .param("equipmentNames", String.join(",", equipmentNames)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }




}