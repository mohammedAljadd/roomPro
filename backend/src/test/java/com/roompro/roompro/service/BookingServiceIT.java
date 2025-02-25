package com.roompro.roompro.service;


import com.roompro.roompro.dto.request.BookingRequestDTO;
import com.roompro.roompro.model.Booking;
import com.roompro.roompro.model.Role;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.model.Users;
import com.roompro.roompro.repository.BookingRepository;
import com.roompro.roompro.repository.RoomRepository;
import com.roompro.roompro.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BookingServiceIT {

    @Autowired
    BookingService bookingService;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    private Users user;
    private Role role;
    private String plainPassword;
    private Optional<Room> room;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    void setUp(){

        role =  new Role(1L, "Admin", "desc", Collections.emptyList());

        user = new Users();
        user.setEmail("test@gmail.com");
        plainPassword = "test";
        user.setPassword(encoder.encode(plainPassword));
        user.setFirstName("fTest");
        user.setLastName("lTest");
        user.setRole(role);

        // register
        userRepository.save(user);

        room = roomRepository.findById(1L);

        // Authenticate the user
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user.getEmail(), plainPassword);
        Authentication authentication = authenticationManager.authenticate(authRequest);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
    }



    @Test
    @Transactional
    void shouldBookSuccessfully() throws Exception {

        // Choose a valid time
        LocalDate firstMonday = LocalDate.of(2050, 1, 1)
                .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        LocalDateTime startTime = LocalDateTime.of(2050, 1, firstMonday.getDayOfMonth(), 8, 0);; // valid time

        long bookingHours = (long) 2.5;
        long bookingMinutes = (long) (bookingHours * 60);
        LocalDateTime endTime = startTime.plusMinutes(bookingMinutes);

        BookingRequestDTO bookingDto = new BookingRequestDTO(room.get().getRoomId(), startTime.toString(), bookingHours);

        bookingService.createBooking(bookingDto);

        // Check if booking created
        List<Booking> bookings = bookingRepository.findByRoomId(1L);

        assertEquals(room.get(), bookings.get(0).getRoom());
        assertEquals(user, bookings.get(0).getUser());
        assertEquals(startTime, bookings.get(0).getStartTime());
        assertEquals(startTime.plusHours((long)2.5), bookings.get(0).getEndTime());
    }


    @Test
    @Transactional
    void shouldThrowExceptionIfBookingTimeInvalid() throws Exception{

        Exception exception;


        // Time outside business hours
        BookingRequestDTO bookingDto = new BookingRequestDTO(room.get().getRoomId(), "2024-02-25T07:00:00", 2.5);
        exception = assertThrows(Exception.class, () -> bookingService.createBooking(bookingDto));
        assertEquals("Booking must be scheduled between 08:00 AM and 06:00 PM.", exception.getMessage());

        // First successfully booking
        LocalDate firstMonday = LocalDate.of(2050, 1, 1)
                .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        LocalDateTime startTime = LocalDateTime.of(2050, 1, firstMonday.getDayOfMonth(), 8, 0); // valid time
        BookingRequestDTO firstBookingDto = new BookingRequestDTO(room.get().getRoomId(), startTime.toString(), 3);
        bookingService.createBooking(firstBookingDto);

        // Second booking that it's time overlap with previous
        startTime = LocalDateTime.of(2050, 1, firstMonday.getDayOfMonth(), 9, 0);; // valid time
        BookingRequestDTO secondBookingDto = new BookingRequestDTO(room.get().getRoomId(), startTime.toString(), 2);

        exception = assertThrows(Exception.class, () -> bookingService.createBooking(secondBookingDto));
        assertEquals("This time slot is already booked.", exception.getMessage());

    }

    @Test
    @Transactional
    void shouldReturnUserBookings() throws Exception{

        // Bookings used in test of returning bookings
        LocalDate firstMonday = LocalDate.of(2055, 1, 1)
                .with(TemporalAdjusters.firstInMonth(DayOfWeek.TUESDAY));
        LocalDateTime startTime1 = LocalDateTime.of(2055, 3, firstMonday.getDayOfMonth(), 8, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2055, 3, firstMonday.getDayOfMonth()+1, 10, 30);
        LocalDateTime startTime3 = LocalDateTime.of(2055, 3, firstMonday.getDayOfMonth()+3, 14, 0);

        BookingRequestDTO bookingDto = new BookingRequestDTO(room.get().getRoomId(), startTime1.toString(), 3);

        bookingService.createBooking(bookingDto);

        bookingDto.setStartTime(startTime2.toString());
        bookingService.createBooking(bookingDto);

        bookingDto.setStartTime(startTime3.toString());
        bookingService.createBooking(bookingDto);



        // Get bookings done in setUp method
        List<Booking> allBookings = bookingRepository.findAll();


        List<Booking> userBookings = bookingService.getUserBookings();

        assertEquals(allBookings, userBookings);

    }

    @Test
    @Transactional
    void shouldThrowExceptionWhenCancelingNonExistingBooking() throws Exception{
        Exception exception = assertThrows(Exception.class, () -> bookingService.cancelBooking(2222222L));
        assertEquals("Booking not found.", exception.getMessage());
    }


    @Test
    @Transactional
    void shouldCancelBookingSuccessfully() throws Exception{
        LocalDate firstMonday = LocalDate.of(2055, 1, 1)
                .with(TemporalAdjusters.firstInMonth(DayOfWeek.TUESDAY));
        LocalDateTime startTime = LocalDateTime.of(2055, 3, firstMonday.getDayOfMonth(), 8, 0);
        BookingRequestDTO bookingDto = new BookingRequestDTO(room.get().getRoomId(), startTime.toString(), 3);
        bookingService.createBooking(bookingDto);
        Booking booking = bookingRepository.findByRoomId(room.get().getRoomId()).get(0);

        Map<String, String> result = bookingService.cancelBooking(booking.getBookingId());

        Map<String, String> expected = new HashMap<>();
        expected.put("message", "Booking cancelled successfully.");

        assertEquals(expected, result);


    }


}
