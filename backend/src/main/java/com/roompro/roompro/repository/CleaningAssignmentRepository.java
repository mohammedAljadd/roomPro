package com.roompro.roompro.repository;


import com.roompro.roompro.model.CleaningType;
import com.roompro.roompro.model.RoomCleaningAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CleaningAssignmentRepository extends JpaRepository<RoomCleaningAssignment, Long> {


    @Query("select cau from RoomCleaningAssignment cau where cau.room.roomId=:roomId and cau.cleaningType.cleaningId=1")
    List<RoomCleaningAssignment> checkIfNeedCleaningAfterUse(long roomId);

    List<RoomCleaningAssignment> findByRoom_RoomId(Long roomId);

}
