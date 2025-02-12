package com.roompro.roompro.repository;

import com.roompro.roompro.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findById(Long roomId);

    List<Room> findAll();

    @Query("SELECT r FROM Room r LEFT JOIN FETCH r.roomEquipmentMappings rem LEFT JOIN FETCH rem.equipment")
    List<Room> findAllWithEquipment();

    @Query("SELECT DISTINCT r FROM Room r " +
            "LEFT JOIN r.roomEquipmentMappings rem " +
            "LEFT JOIN rem.equipment e " +
            "WHERE (:capacity IS NULL OR r.capacity >= :capacity) " +
            "AND (:location IS NULL OR r.location LIKE %:location%) " +
            "AND (:equipmentList IS NULL OR " +
            "     (SELECT COUNT(DISTINCT rem2.equipment.equipmentId) FROM RoomEquipmentMapping rem2 " +
            "      WHERE rem2.room.roomId = r.roomId AND rem2.equipment.name IN :equipmentList) = " +
            "     (SELECT COUNT(DISTINCT e3.name) FROM Equipment e3 WHERE e3.name IN :equipmentList))")
    List<Room> findAllWithFilters(Integer capacity, String location, List<String> equipmentList);
}
