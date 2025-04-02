package com.roompro.roompro.repository;

import com.roompro.roompro.model.CleaningWeekly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface CleaningWeeklyRepository extends JpaRepository<CleaningWeekly, Long> {

    @Query("select c from CleaningWeekly c where c.room.roomId=:roomId")
    List<CleaningWeekly> findByRoomId(long roomId);

    @Query("SELECT COUNT(cw) > 0 FROM CleaningWeekly cw WHERE cw.room.id = :roomId AND " +
            "(cw.starttime < :endTime AND cw.endtime > :startTime)")
    boolean isOverlappingWithCleaningSlots(long roomId, LocalTime startTime, LocalTime endTime);

}