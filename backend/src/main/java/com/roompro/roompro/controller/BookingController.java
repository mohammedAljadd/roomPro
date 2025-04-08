package com.roompro.roompro.controller;


import com.roompro.roompro.dto.request.BookingRequestDTO;
import com.roompro.roompro.dto.response.BookingResponseDTO;
import com.roompro.roompro.dto.response.BookingTrendsResponseDTO;
import com.roompro.roompro.model.Booking;
import com.roompro.roompro.service.BookingService;
import com.roompro.roompro.service.CleaningService;
import com.roompro.roompro.service.mapper.BookingMapperImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roompro")
@CrossOrigin(origins = "http://localhost:4209")
public class BookingController {

    @Autowired
    BookingService bookingService;



    private BookingMapperImp bookingMapper = new BookingMapperImp();


    @PostMapping("/booking")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO bookingDto) {

        if (bookingDto.getStartTime() == null || bookingDto.getStartTime().isEmpty() ||
                bookingDto.getBookingHours() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All fields are required.");
        }

        try{
             bookingService.createBooking(bookingDto);



            Map<String, String> response = new HashMap<>();
            response.put("message", "Your booking was successful! Thank you for using our service.");
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @GetMapping("/my-bookings")
    public List<BookingResponseDTO> getUserBookings(){
        List<Booking> bookings = bookingService.getUserBookings();
        return bookings.stream()
                .map(bookingMapper::bookingToBookingResponseDTO)
                .collect(Collectors.toList());

    }

    @GetMapping("/booking/room/{room_id}")
    public List<BookingResponseDTO> getARoomBooking(@PathVariable Long room_id){
        List<Booking> bookings = bookingService.getBookingsByRoomId(room_id);
        return bookings.stream()
                .map(bookingMapper::bookingToBookingResponseDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/my-bookings/cancel/{booking_id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long booking_id) {
        
        try{
            Map<String, String>  response = bookingService.cancelBooking(booking_id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        
    }


    @GetMapping("/booking/trends/{year}/{month}")
    public BookingTrendsResponseDTO getBookingTrends(@PathVariable int year, @PathVariable int month){
        BookingTrendsResponseDTO trends = bookingService.getBookingTrends(year, month);
        return trends;
    }
}
