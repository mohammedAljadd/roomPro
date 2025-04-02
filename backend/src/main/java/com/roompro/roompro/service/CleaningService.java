package com.roompro.roompro.service;


import com.roompro.roompro.model.CleaningAfterUse;
import com.roompro.roompro.model.CleaningWeekly;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.model.RoomCleaningAssignment;
import com.roompro.roompro.repository.AfterUseCleaningRepository;
import com.roompro.roompro.repository.CleaningAssignmentRepository;
import com.roompro.roompro.repository.CleaningWeeklyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CleaningService {

    @Autowired
    CleaningAssignmentRepository cleaningRepository;

    @Autowired
    AfterUseCleaningRepository afterUseCleaningRepository;

    @Autowired
    CleaningWeeklyRepository cleaningWeeklyRepository;

    public List<CleaningAfterUse> getAllAfterUseCleaning(long roomId){
        return afterUseCleaningRepository.findByRoomId(roomId);
    }

    public List<CleaningWeekly> getAllWeeklyCleaning(long roomId){
        return cleaningWeeklyRepository.findByRoomId(roomId);
    }


    public boolean checkIfNeedCleaningAfterUse(long roomId){
       List<RoomCleaningAssignment> result = cleaningRepository.checkIfNeedCleaningAfterUse(roomId);
       return !result.isEmpty();
    }


    public void setCleaningSlot(LocalDateTime endDateTime, Room room){
        CleaningAfterUse cleaningAfterUse = new CleaningAfterUse();
        cleaningAfterUse.setRoom(room);
        cleaningAfterUse.setStartTime(endDateTime);
        cleaningAfterUse.setEndTime(endDateTime.plusMinutes(20)); // cleaning for 20 minutes

        afterUseCleaningRepository.save(cleaningAfterUse);

    }

    public void deleteAfterUseCleaningSlots(long roomId){
        afterUseCleaningRepository.deleteByRoom_RoomId(roomId);
    }
}
