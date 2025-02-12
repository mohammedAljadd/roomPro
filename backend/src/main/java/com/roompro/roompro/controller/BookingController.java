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
        System.out.println(bookingDto);

        Map<String, String> response = bookingService.createBooking(bookingDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-bookings")
    public List<Booking> getUserBookings(){

        return bookingService.getUserBookings();
    }
}
