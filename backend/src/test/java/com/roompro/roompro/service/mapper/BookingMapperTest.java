package com.roompro.roompro.service.mapper;

import com.roompro.roompro.dto.response.BookingResponseDTO;
import com.roompro.roompro.dto.response.RoomResponseDTO;
import com.roompro.roompro.model.Booking;
import com.roompro.roompro.model.Role;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.model.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BookingMapperTest {

    private RoomMapper roomMapper = new RoomMapperImpl();

    private BookingMapperImp bookingMapper = new BookingMapperImp();



    private Users user;
    private Room room;
    private LocalDateTime start;
    private LocalDateTime end;


    @BeforeEach
    void setUp() {
        Role role = new Role(52L, "Admin", "Admin with full access", Collections.emptyList());
        user = new Users(1L, "test@gmail.com", "testpassword", "Said", "Hamza", role);
        room = new Room(1L, "Room1", (short) 50, "5th floor", "Very large room", Collections.emptyList(), Collections.emptyList());
        start =  LocalDateTime.of(2025, 2, 19, 8, 0);
        end =  LocalDateTime.of(2025, 2, 19, 10, 30);

    }

    @Test
    public void shouldMapBookingEntToBookingDTO(){

        Booking booking = new Booking(2L, user, room,  start, end);

        BookingResponseDTO bookingDTO = bookingMapper.bookingToBookingResponseDTO(booking);

        RoomResponseDTO roomDTO = roomMapper.roomToRoomResponseDTO(room);

        assertEquals(2L, bookingDTO.getBookingId());
        assertEquals(start, LocalDateTime.of(2025, 2, 19, 8, 0));
        assertEquals(end, LocalDateTime.of(2025, 2, 19, 10, 30));
        assertEquals(roomDTO, bookingDTO.getRoom());
    }

    @Test
    public void shouldMapBookingEntToBookingDTONullDates(){
        Booking booking = new Booking(2L, user, room,  null, null);
        BookingResponseDTO bookingDTO = bookingMapper.bookingToBookingResponseDTO(booking);

        assertNull(bookingDTO.getStartTime());
        assertNull(bookingDTO.getEndTime());
    }

    @Test
    public void shouldMapBookingEntToBookingDTONullUser(){
        Booking booking = new Booking(982L, null, room,  start, end);
        BookingResponseDTO bookingDTO = bookingMapper.bookingToBookingResponseDTO(booking);
        RoomResponseDTO roomDTO = roomMapper.roomToRoomResponseDTO(room);


        assertEquals(982L, bookingDTO.getBookingId());
        assertEquals(roomDTO, bookingDTO.getRoom());
        assertNull(bookingDTO.getUserEmail());

    }

}