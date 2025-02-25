package com.roompro.roompro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roompro.roompro.config.TestSecurityConfig;
import com.roompro.roompro.dto.request.BookingRequestDTO;
import com.roompro.roompro.dto.response.BookingResponseDTO;
import com.roompro.roompro.model.Booking;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.model.Users;
import com.roompro.roompro.service.BookingService;
import com.roompro.roompro.service.JWTService;
import com.roompro.roompro.service.mapper.BookingMapperImp;
import com.roompro.roompro.service.mapper.RoomMapper;
import com.roompro.roompro.service.mapper.RoomMapperImpl;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class BookingControllerTest {


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private JWTService jwtService;

    private BookingMapperImp bookingMapper = new BookingMapperImp();


    private RoomMapper roomMapper = new RoomMapperImpl();



    @Test
    void shouldCreateBookingSuccessfully() throws Exception {
        BookingRequestDTO bookingDto = new BookingRequestDTO(1L, "2025-02-25T10:00:00", 2.0);

        doNothing().when(bookingService).createBooking(bookingDto);

        mockMvc.perform(post("/roompro/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Your booking was successful! Thank you for using our service."));

    }

    @Test
    void shouldReturnBadRequestWhenMissingFields() throws Exception {
        BookingRequestDTO bookingDto = new BookingRequestDTO(1L, "", 0);


        doNothing().when(bookingService).createBooking(bookingDto);

        mockMvc.perform(post("/roompro/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("All fields are required."));
    }


    @Test
    void shouldReturnBadRequestWhenServiceThrowsException() throws Exception {
        BookingRequestDTO bookingDto = new BookingRequestDTO(1L, "2025-02-25T10:00:00", 2.0);

        doThrow(new Exception("Booking creation failed.")).when(bookingService).createBooking(bookingDto);

        mockMvc.perform(post("/roompro/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Booking creation failed."));
    }

    @Test
    void shouldReturnUserBookings() throws Exception {

        Room room = new Room(56L, "Room1", (short)45, "1st floor", "description", Collections.emptyList(), Collections.emptyList());

        Users user = new Users();
        user.setUserId(1L);
        user.setEmail("test@gmail.com");
        LocalDateTime startTime = LocalDateTime.now();

        Booking booking1 = new Booking(1L, user, room, startTime, startTime.plusHours(2));
        Booking booking2 = new Booking(2L, user, room, startTime, startTime.plusHours(4));
        Booking booking3 = new Booking(3L, user, room, startTime, startTime.plusHours(3));


        List<Booking> bookings = List.of(booking1, booking2, booking3);
        when(bookingService.getUserBookings()).thenReturn(bookings);
        List<BookingResponseDTO> bookingResponseDTOs = bookings.stream()
                .map(bookingMapper::bookingToBookingResponseDTO)
                .collect(Collectors.toList());

        String expectedJson = objectMapper.writeValueAsString(bookingResponseDTOs);

        mockMvc.perform(get("/roompro/my-bookings"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }


    @Test
    void shouldReturnRoomBookings() throws Exception {

        Long roomId = 56L;
        Room room = new Room(56L, "Room1", (short)45, "1st floor", "description", Collections.emptyList(), Collections.emptyList());
        Room room2 = new Room(57L, "Room2", (short)50, "1st floor", "description", Collections.emptyList(), Collections.emptyList());

        Users user = new Users();
        user.setUserId(1L);
        user.setEmail("test@gmail.com");
        LocalDateTime startTime = LocalDateTime.now();

        Booking booking1 = new Booking(1L, user, room, startTime, startTime.plusHours(2));
        Booking booking2 = new Booking(2L, user, room, startTime, startTime.plusHours(4));
        Booking booking3 = new Booking(3L, user, room2, startTime, startTime.plusHours(3));

        List<Booking> bookings = List.of(booking1, booking2, booking3);

        bookings.stream().filter(booking -> booking.getRoom().getRoomId() == roomId).collect(Collectors.toList());

        when(bookingService.getBookingsByRoomId(roomId)).thenReturn(bookings);
        List<BookingResponseDTO> bookingResponseDTOs = bookings.stream()
                .map(bookingMapper::bookingToBookingResponseDTO)
                .collect(Collectors.toList());

        String expectedJson = objectMapper.writeValueAsString(bookingResponseDTOs);


        mockMvc.perform(get("/roompro/bookings/room/{room_id}", roomId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldCancelBookingSuccessfully() throws Exception {
        Long bookingId = 1L;

        when(bookingService.cancelBooking(bookingId)).thenReturn(Map.of("message", "Booking cancelled successfully."));
        mockMvc.perform(delete("/roompro/my-bookings/cancel/{booking_id}", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Booking cancelled successfully."));
    }

    @Test
    void shouldReturnBadRequestIfCancelBookingfail() throws Exception {
        Long bookingId = 1L;

        when(bookingService.cancelBooking(bookingId)).thenThrow(new Exception("Booking not found."));
        mockMvc.perform(delete("/roompro/my-bookings/cancel/{booking_id}", bookingId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Booking not found."));
    }
}