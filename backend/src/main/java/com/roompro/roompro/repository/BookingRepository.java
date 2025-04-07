package com.roompro.roompro.repository;


import com.roompro.roompro.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {



    @Query("SELECT b FROM Booking b WHERE b.room.roomId = :roomId " +
            "AND ((b.startTime BETWEEN :startTime AND :endTime) " +
            "OR (b.endTime BETWEEN :startTime AND :endTime))")
    List<Booking> findOverlappingBookings(Long roomId, LocalDateTime startTime, LocalDateTime endTime);

    List<Booking> findAll();


    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId")
    List<Booking> findUserBookings(Long userId);


    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId")
    List<Booking> findByRoomId(long roomId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId AND " +
            "(b.startTime < :endTime AND b.endTime > :startTime)")
    boolean isOverlappingWithOtherBookings(long roomId, LocalDateTime startTime, LocalDateTime endTime);

    @Query(value = "SELECT COUNT(*) FROM bookings WHERE EXTRACT(YEAR FROM start_time) = :year AND EXTRACT(MONTH FROM start_time) = :month", nativeQuery = true)
    int findTotalBookings(int year, int month);

    @Query(value = "select EXTRACT(HOUR FROM b.start_time) as hour, count(b.start_time) as bookings_count  from bookings b " +
            "where EXTRACT(YEAR FROM start_time) = :year " +
            "and EXTRACT(MONTH FROM start_time) = :month " +
            "group by EXTRACT(HOUR FROM b.start_time) " +
            "order by count(b.start_time) desc " +
            "limit 1", nativeQuery = true)
    List<Object []> findPeakHour(int year, int month);


    @Query(value = "select EXTRACT(dow  FROM b.start_time) as dayOfWeek, count(EXTRACT(dow  FROM b.start_time)) as booking_count  from bookings b " +
            "where EXTRACT(YEAR FROM start_time) = :year " +
            "and EXTRACT(MONTH FROM start_time) = :month  " +
            "group by dayOfWeek " +
            "order by booking_count desc " +
            "limit 1", nativeQuery = true)
    List<Object []> findPeakDay(int year, int month);

    @Query(value = "SELECT " +
            "SUM(EXTRACT(EPOCH FROM b.end_time - b.start_time)) / 3600.0 / COUNT(*) AS avg_duration_hours " +
            "FROM bookings b " +
            "where EXTRACT(YEAR FROM start_time) = :year " +
            "and EXTRACT(MONTH FROM start_time) = :month", nativeQuery = true)
    List<Object []> findAverageDuration(int year, int month);


}
