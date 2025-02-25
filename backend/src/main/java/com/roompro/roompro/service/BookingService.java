package com.roompro.roompro.service;


import com.roompro.roompro.dto.request.BookingRequestDTO;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;



    public void createBooking(BookingRequestDTO bookingDto) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Room room = roomRepository.findById(bookingDto.getRoomId()).orElse(null);
        LocalDateTime startDateTime = LocalDateTime.parse(bookingDto.getStartTime());
        double bookingHours = bookingDto.getBookingHours();
        long bookingMinutes = (long) (bookingHours * 60);
        LocalDateTime endDateTime = startDateTime.plusMinutes(bookingMinutes);


        LocalTime businessStart = LocalTime.of(8, 0);  // 8:00 AM
        LocalTime businessEnd = LocalTime.of(18, 0);

        if (startDateTime.toLocalTime().isBefore(businessStart) ||
                endDateTime.toLocalTime().isAfter(businessEnd) ||
                startDateTime.toLocalTime().isAfter(businessEnd) ||
                endDateTime.toLocalTime().isBefore(businessStart)) {

            throw new Exception("Booking must be scheduled between 08:00 AM and 06:00 PM.");
        }


        if(bookingRepository.isOverlappingWithOtherBookings(room.getRoomId(), startDateTime, endDateTime)){
            throw new Exception("This time slot is already booked.");
        }



        Users user = userRepository.findByEmail(email);
        Booking newBooking = new Booking();
        newBooking.setRoom(room);
        newBooking.setUser(user);

        newBooking.setStartTime(startDateTime);
        newBooking.setEndTime(endDateTime);

        bookingRepository.save(newBooking);
    }

    public List<Booking> getUserBookings(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users user = userRepository.findByEmail(email);
        return bookingRepository.findUserBookings(user.getUserId());
    }

    public List<Booking> getBookingsByRoomId(Long room_id){
        return bookingRepository.findByRoomId(room_id);

    }

    public Map<String, String> cancelBooking(Long id) throws Exception {

        if(bookingRepository.findById(id).isEmpty()){
            throw new Exception("Booking not found.");
        }

        bookingRepository.deleteById(id);
        return Map.of("message", "Booking cancelled successfully.");

    }
}
