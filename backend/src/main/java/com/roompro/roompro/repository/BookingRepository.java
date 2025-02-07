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



}
