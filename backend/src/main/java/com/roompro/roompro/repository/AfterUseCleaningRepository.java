package com.roompro.roompro.repository;

import com.roompro.roompro.model.CleaningAfterUse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface AfterUseCleaningRepository extends JpaRepository<CleaningAfterUse, Long> {

    @Query("select c from CleaningAfterUse c where c.room.roomId=:roomId")
    List<CleaningAfterUse> findByRoomId(long roomId);


    @Transactional
    void deleteByRoom_RoomId(Long roomId);

    @Query("SELECT COUNT(cau) > 0 FROM CleaningAfterUse cau WHERE cau.room.id = :roomId AND " +
            "(cau.startTime < :endTime AND cau.endTime > :startTime)")
    boolean isOverlappingWithCleaningSlots(long roomId, LocalDateTime startTime, LocalDateTime endTime);
}

