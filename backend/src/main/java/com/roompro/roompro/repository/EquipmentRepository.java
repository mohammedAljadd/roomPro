package com.roompro.roompro.repository;


import com.roompro.roompro.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    @Query("SELECT e, " +
            "CASE WHEN EXISTS (SELECT 1 FROM RoomEquipmentMapping rem " +
            "WHERE rem.equipment.id = e.id AND rem.room.roomId = :roomId) " +
            "THEN true ELSE false END " +
            "FROM Equipment e")
    List<Object[]>  findAllEquipments(Long roomId);
}
