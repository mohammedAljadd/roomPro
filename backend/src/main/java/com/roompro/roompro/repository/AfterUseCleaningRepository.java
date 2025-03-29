package com.roompro.roompro.repository;

import com.roompro.roompro.model.CleaningAfterUse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AfterUseCleaningRepository extends JpaRepository<CleaningAfterUse, Long> {

    @Query("select c from CleaningAfterUse c where c.room.roomId=:roomId")
    List<CleaningAfterUse> findByRoomId(long roomId);


    @Transactional
    void deleteByRoom_RoomId(Long roomId);

}

