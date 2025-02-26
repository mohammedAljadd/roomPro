package com.roompro.roompro.repository;


import com.roompro.roompro.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    @Query("SELECT DISTINCT e, CASE WHEN rem.room.roomId = :roomId THEN true ELSE false END AS isAvailable " +
            "FROM Equipment e " +
            "LEFT JOIN RoomEquipmentMapping rem ON rem.equipment.id = e.id")
    List<Object[]>  findAllEquipments(Long roomId);
}
