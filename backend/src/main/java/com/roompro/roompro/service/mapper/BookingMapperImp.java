package com.roompro.roompro.service.mapper;

import com.roompro.roompro.dto.response.BookingResponseDTO;
import com.roompro.roompro.dto.response.RoomResponseDTO;
import com.roompro.roompro.model.Booking;
import com.roompro.roompro.model.Users;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BookingMapperImp {

    private RoomMapper roomMapper = new RoomMapperImpl();


    public BookingResponseDTO bookingToBookingResponseDTO(Booking booking) {
        if ( booking == null ) {
            return null;
        }

        RoomResponseDTO room = null;
        String userEmail = null;
        Long bookingId = null;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        boolean isCanceled = false;

        room = roomMapper.roomToRoomResponseDTO( booking.getRoom() );
        userEmail = bookingUserEmail( booking );
        bookingId = booking.getBookingId();
        startTime = booking.getStartTime();
        endTime = booking.getEndTime();
        isCanceled = booking.isCanceled();

        BookingResponseDTO bookingResponseDTO = new BookingResponseDTO( bookingId, room, startTime, endTime, userEmail, isCanceled );

        return bookingResponseDTO;
    }

    private String bookingUserEmail(Booking booking) {
        Users user = booking.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getEmail();
    }
}

