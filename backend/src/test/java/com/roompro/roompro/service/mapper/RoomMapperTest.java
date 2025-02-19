package com.roompro.roompro.service.mapper;


import com.roompro.roompro.dto.response.RoomResponseDTO;
import com.roompro.roompro.model.Equipment;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.model.RoomEquipmentMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;

class RoomMapperTest {

    private RoomMapper roomMapper;

    @BeforeEach
    void setUp() {
        roomMapper = Mappers.getMapper(RoomMapper.class);
    }

    @Test
    public void shouldMapRoomEntToRoomDTO(){
        // Room
        Room room = new Room(1L, "Room1", (short) 50, "5th floor", "Very large room", Collections.emptyList(), Collections.emptyList());

        // Add equipment mapping
        RoomEquipmentMapping roomEquipmentMapping1 = new RoomEquipmentMapping();
        roomEquipmentMapping1.setRoom(room);
        roomEquipmentMapping1.setEquipment(new Equipment(45L, "Projector", null, null, null));

        RoomEquipmentMapping roomEquipmentMapping2 = new RoomEquipmentMapping();
        roomEquipmentMapping2.setRoom(room);
        roomEquipmentMapping2.setEquipment(new Equipment(46L, "Whiteboard", null, null, null));

        room.setRoomEquipmentMappings(Arrays.asList(roomEquipmentMapping1, roomEquipmentMapping2));

        // Map to DTO using the mapper
        RoomResponseDTO dto = roomMapper.roomToRoomResponseDTO(room);

        // Assertions
        assertNotNull(dto);
        assertEquals(1L, dto.getRoomId());
        assertEquals("Room1", dto.getName());
        assertEquals((short) 50, dto.getCapacity());
        assertEquals("5th floor", dto.getLocation());
        assertEquals("Very large room", dto.getDescription());
        assertNotNull(dto.getEquipments());
        assertTrue(dto.getEquipments().contains("Projector"));
        assertTrue(dto.getEquipments().contains("Whiteboard"));
    }

    @Test
    public void shouldMapRoomEntToRoomDTOWithNoEquipments() {
        Room room = new Room(2L, "Room2", (short) 9, "1st floor", "Small room", Collections.emptyList(), Collections.emptyList());

        RoomResponseDTO dto = roomMapper.roomToRoomResponseDTO(room);

        assertNotNull(dto);
        assertEquals(2L, dto.getRoomId());
        assertEquals("Room2", dto.getName());
        assertEquals((short) 9, dto.getCapacity());
        assertEquals("1st floor", dto.getLocation());
        assertEquals("Small room", dto.getDescription());
        assertNotNull(dto.getEquipments());
        assertTrue(dto.getEquipments().isEmpty());
    }



    @Test
    public void shouldMapRoomWithNullValues() {
        Room room = new Room(2L, null, 0, null, "Small room", Collections.emptyList(), Collections.emptyList());
        RoomResponseDTO dto = roomMapper.roomToRoomResponseDTO(room);
        assertNotNull(dto);
        assertNull(dto.getName());
        assertEquals((short)0, dto.getCapacity());
        assertNull(dto.getLocation());
    }

    @Test
    public void shouldMapRoomWithNullEquipmentsList(){
        Room room = new Room(1L, "room", 10, "Floor1", "Small room", Collections.emptyList(), null);
        RoomResponseDTO dto = roomMapper.roomToRoomResponseDTO(room);
        assertNull(dto.getEquipments());
    }

    @Test
    public void shouldMapRoomWithMultipleEquipments(){
        Room room = new Room(1L, "room", 10, "Floor1", "Small room", Collections.emptyList(), Collections.emptyList());
        // Add equipment mapping
        RoomEquipmentMapping roomEquipmentMapping1 = new RoomEquipmentMapping();
        roomEquipmentMapping1.setRoom(room);
        roomEquipmentMapping1.setEquipment(new Equipment(45L, "Projector", null, null, null));

        RoomEquipmentMapping roomEquipmentMapping2 = new RoomEquipmentMapping();
        roomEquipmentMapping2.setRoom(room);
        roomEquipmentMapping2.setEquipment(new Equipment(46L, "Whiteboard", null, null, null));

        RoomEquipmentMapping roomEquipmentMapping3 = new RoomEquipmentMapping();
        roomEquipmentMapping3.setRoom(room);
        roomEquipmentMapping3.setEquipment(new Equipment(47L, "Sound system", null, null, null));

        RoomEquipmentMapping roomEquipmentMapping4 = new RoomEquipmentMapping();
        roomEquipmentMapping4.setRoom(room);
        roomEquipmentMapping4.setEquipment(new Equipment(48L, "Lightning stage", null, null, null));

        room.setRoomEquipmentMappings(Arrays.asList(roomEquipmentMapping1, roomEquipmentMapping2, roomEquipmentMapping3, roomEquipmentMapping4));

        RoomResponseDTO dto = roomMapper.roomToRoomResponseDTO(room);


        System.out.println(dto.getEquipments());
        assertTrue(dto.getEquipments().contains("Projector"));
        assertTrue(dto.getEquipments().contains("Whiteboard"));
        assertTrue(dto.getEquipments().contains("Sound system"));
        assertTrue(dto.getEquipments().contains("Lightning stage"));


    }



}