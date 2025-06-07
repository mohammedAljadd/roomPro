package com.roompro.roompro.service;


import com.roompro.roompro.dto.request.BookingRequestDTO;
import com.roompro.roompro.dto.request.HolidayDTO;
import com.roompro.roompro.dto.response.BookingTrendsResponseDTO;
import com.roompro.roompro.dto.response.MostBookedRoomResponseDTO;
import com.roompro.roompro.model.Booking;
import com.roompro.roompro.model.Maintenance;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.model.Users;
import com.roompro.roompro.repository.BookingRepository;
import com.roompro.roompro.repository.CleaningAssignmentRepository;
import com.roompro.roompro.repository.RoomRepository;
import com.roompro.roompro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    CleaningService cleaningService;

    @Autowired
    MaintenanceService maintenanceService;

    @Autowired
    CleaningAssignmentRepository cleaningAssignmentRepository;

    @Autowired
    HolidayService holidayService;

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


        if(startDateTime.getDayOfWeek()== DayOfWeek.SATURDAY || startDateTime.getDayOfWeek()==DayOfWeek.SUNDAY){
            throw new Exception("Bookings are available Monday through Friday. Please choose a weekday to continue.");
        }


        if(bookingRepository.isOverlappingWithOtherBookings(room.getRoomId(), startDateTime, endDateTime)){
            throw new Exception("This time slot is already booked.");
        }

        // Check if overlap with cleaning slots
        long cleaningId = cleaningAssignmentRepository.findByRoom_RoomId(room.getRoomId()).getFirst().getCleaningType().getCleaningId();
        boolean isOverlapping = false;
        if(cleaningId==2){

            // Cleaning day :
            String cleaningDay = cleaningService.cleaningWeeklyRepository.findByRoomId(room.getRoomId()).getFirst().getCleaningDay();
            String bookingDay = startDateTime.getDayOfWeek().toString();

            if(cleaningDay.toLowerCase().equals(bookingDay.toLowerCase())){
                isOverlapping = cleaningService.cleaningWeeklyRepository.isOverlappingWithCleaningSlots(room.getRoomId(), startDateTime.toLocalTime(), endDateTime.toLocalTime());
            }
        } else if (cleaningId == 1) {
            isOverlapping = cleaningService.afterUseCleaningRepository.isOverlappingWithCleaningSlots(room.getRoomId(), startDateTime, endDateTime);
        }



        if(isOverlapping){
            throw new Exception("This time slot is overlapping with a cleaning slot.");
        }


        // Check if booking in maintenance
        boolean isOverlappingWithMaintenance = maintenanceService.isOverlappingWithMaintenanceSlots(room.getRoomId(), startDateTime, endDateTime);
        if(isOverlappingWithMaintenance){
            throw new Exception("Looks like the room is under maintenance during this time. Please select a time outside the maintenance window.");
        }


        // Check if booking scheduled on a holiday day
        String year = String.valueOf(LocalDate.now().getYear());
        List<HolidayDTO> holidays = holidayService.getHolidays(year, "FR");

        for (HolidayDTO holiday : holidays) {
            String holidayDate = holiday.getDate();
            String bookingDate = bookingDto.getStartTime().split("T")[0];

            if (holidayDate.equals(bookingDate)) {
                throw new Exception("Booking can't be scheduled on a holiday: " + holiday.getName());
            }
        }


        Users user = userRepository.findByEmail(email);
        Booking newBooking = new Booking();


        newBooking.setRoom(room);
        newBooking.setUser(user);

        newBooking.setStartTime(startDateTime);
        newBooking.setEndTime(endDateTime);

        System.out.println(newBooking.getBookingId());
        bookingRepository.save(newBooking);


        // Check if room needs cleaning after use
        if(cleaningService.checkIfNeedCleaningAfterUse(bookingDto.getRoomId())){
            // Add cleaning slot
            cleaningService.setCleaningSlot(endDateTime, room);
        }


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

        Booking booking = bookingRepository.findById(id).get();
        booking.setCanceled(true);

        bookingRepository.save(booking);

        return Map.of("message", "Booking cancelled successfully.");

    }

    public BookingTrendsResponseDTO getBookingTrends(int year, int month){
        BookingTrendsResponseDTO trends = new BookingTrendsResponseDTO();

        // Total Bookings
        int totalBookings = bookingRepository.findTotalBookings(year, month);

        // Peak Hour
        List<Object []> peakHourAndBookingNumber = bookingRepository.findPeakHour(year, month);

        // Check if the array is not empty
        if (peakHourAndBookingNumber != null) {
            // Print the type of each element
            for (int i = 0; i < peakHourAndBookingNumber.size(); i++) {
                int peakHour = ((BigDecimal) peakHourAndBookingNumber.get(i)[0]).intValue();
                int bookingNumber = ((Long) peakHourAndBookingNumber.get(i)[1]).intValue();
                trends.setPeakHour(peakHour+":"+bookingNumber);
            }
        }

        // Most booked room
        List<Object []> mostBookedRoomList = roomRepository.findMostBookedRoom(year, month);

        MostBookedRoomResponseDTO mostBookedRoom = new MostBookedRoomResponseDTO();
        if (!mostBookedRoomList.isEmpty()){

            Object[] row = mostBookedRoomList.get(0);

            String roomName = (String) row[4];
            mostBookedRoom.setName((String) row[4]);
            mostBookedRoom.setDescription((String) row[2]);
            mostBookedRoom.setCapacity(((Number) row[1]).shortValue());
            mostBookedRoom.setLocation((String) row[3]);
            mostBookedRoom.setBooking_count((Long) row[5]);
        }
        trends.setMostBookedRoom(mostBookedRoom);


        // Peak day
        List<Object []> peakDayList = bookingRepository.findPeakDay(year, month);
        List<String> daysOfWeek = Arrays.asList(
                "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        );

        if(!peakDayList.isEmpty()){
            int indexPeakDay = ((BigDecimal) peakDayList.getFirst()[0]).intValue();
            long peakDayBookingCount = ((Number)  peakDayList.getFirst()[1]).longValue();
            trends.setPeakDay(daysOfWeek.get(indexPeakDay)+":"+peakDayBookingCount);
        }

        // Average booking duration
        List<Object[]> avgBookingDurationList = bookingRepository.findAverageDuration(year, month);

        if(!avgBookingDurationList.isEmpty()){
            double avgBookingDuration = ((BigDecimal) avgBookingDurationList.getFirst()[0]).doubleValue();
            avgBookingDuration =  Math.floor(avgBookingDuration * 100) / 100;

            trends.setAverageBookingDuration(avgBookingDuration);
        }


        trends.setTotalBookings(totalBookings);

        return trends;
    }


}
