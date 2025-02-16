package com.roompro.roompro.controller;


import com.roompro.roompro.dto.BookingDto;
import com.roompro.roompro.model.Booking;
import com.roompro.roompro.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/roompro")
@CrossOrigin(origins = "http://localhost:4209")
public class BookingController {

    @Autowired
    BookingService bookingService;

    @PostMapping("/booking")
    public ResponseEntity<Map<String, String>> createBooking(@RequestBody BookingDto bookingDto) {
        Map<String, String> response = bookingService.createBooking(bookingDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-bookings")
    public List<Booking> getUserBookings(){
        return bookingService.getUserBookings();
    }

    @GetMapping("/bookings/room/{room_id}")
    public List<Booking> getARoomBooking(@PathVariable Long room_id){
        return bookingService.getBookingsByRoomId(room_id);
    }

    @DeleteMapping("/my-bookings/cancel/{booking_id}")
    public ResponseEntity<Map<String, String>> cancelBooking(@PathVariable Long booking_id){
        Map<String, String> response = Map.of("message", "Deleted");
        bookingService.cancelBooking(booking_id);
        return ResponseEntity.ok(response);
    }
}
