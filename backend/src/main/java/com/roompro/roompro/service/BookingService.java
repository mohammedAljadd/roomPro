package com.roompro.roompro.service;


import com.roompro.roompro.dto.BookingDto;
import com.roompro.roompro.model.Booking;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.model.Users;
import com.roompro.roompro.repository.BookingRepository;
import com.roompro.roompro.repository.RoomRepository;
import com.roompro.roompro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;



    public Map<String, String> createBooking(BookingDto bookingDto){


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Room room = roomRepository.findById(bookingDto.getRoomId()).orElse(null);

        Users user = userRepository.findByEmail(email);
        Booking newBooking = new Booking();
        newBooking.setRoom(room);
        newBooking.setUser(user);
        LocalDateTime startDateTime = LocalDateTime.parse(bookingDto.getStartTime());
        LocalDateTime endDateTime = startDateTime.plusHours(bookingDto.getHours());
        newBooking.setStartTime(startDateTime);
        newBooking.setEndTime(endDateTime);

        bookingRepository.save(newBooking);

        return Map.of("message", "Booking done");
    }
}
