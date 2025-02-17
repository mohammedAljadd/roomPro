package com.roompro.roompro.controller;


import com.roompro.roompro.dto.request.BookingRequestDTO;
import com.roompro.roompro.dto.response.BookingResponseDTO;
import com.roompro.roompro.model.Booking;
import com.roompro.roompro.service.BookingService;
import com.roompro.roompro.service.mapper.BookingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roompro")
@CrossOrigin(origins = "http://localhost:4209")
public class BookingController {

    @Autowired
    BookingService bookingService;

    @Autowired
    BookingMapper bookingMapper;

    @PostMapping("/booking")
    public ResponseEntity<Map<String, String>> createBooking(@RequestBody BookingRequestDTO bookingDto) {
        Map<String, String> response = bookingService.createBooking(bookingDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-bookings")
    public List<BookingResponseDTO> getUserBookings(){
        List<Booking> bookings = bookingService.getUserBookings();
        return bookings.stream()
                .map(bookingMapper::bookingToBookingResponseDTO)
                .collect(Collectors.toList());

    }

    @GetMapping("/bookings/room/{room_id}")
    public List<BookingResponseDTO> getARoomBooking(@PathVariable Long room_id){
        List<Booking> bookings = bookingService.getBookingsByRoomId(room_id);
        return bookings.stream()
                .map(bookingMapper::bookingToBookingResponseDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/my-bookings/cancel/{booking_id}")
    public ResponseEntity<Map<String, String>> cancelBooking(@PathVariable Long booking_id){
        Map<String, String> response = Map.of("message", "Deleted");
        bookingService.cancelBooking(booking_id);
        return ResponseEntity.ok(response);
    }
}
