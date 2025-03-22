package com.roompro.roompro.repository;

import com.roompro.roompro.model.RoomEquipmentMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EquipmentMappingRepository  extends JpaRepository<RoomEquipmentMapping, Long> {


    @Query("SELECT rem FROM RoomEquipmentMapping rem WHERE rem.room.roomId = :roomId AND rem.equipment.id = :equipmentId")
    List<RoomEquipmentMapping> findByRoomIdAndEquipmentId(Long equipmentId, Long roomId);
}
