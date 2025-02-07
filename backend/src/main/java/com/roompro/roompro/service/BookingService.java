package com.roompro.roompro.service;


import com.roompro.roompro.dto.BookingDto;
import com.roompro.roompro.model.Booking;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.model.Users;
import com.roompro.roompro.repository.BookingRepository;
import com.roompro.roompro.repository.RoomRepository;
import com.roompro.roompro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;



    public void createBooking(BookingDto bookingDto){


        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(bookingDto.getRoomId(),
                bookingDto.getStartTime(), bookingDto.getEndTime());

        if (!overlappingBookings.isEmpty()) {
            throw new RuntimeException("The room is already booked during this time.");
        }

        Room room = roomRepository.findById(bookingDto.getRoomId()).orElse(null);
        Long userId = 1L;
        Users user = userRepository.findById(userId).orElse(null);
        Booking newBooking = new Booking();
        newBooking.setRoom(room);
        newBooking.setUser(user);
        newBooking.setStartTime(bookingDto.getStartTime());
        newBooking.setEndTime(bookingDto.getEndTime());

        bookingRepository.save(newBooking);

    }
}
