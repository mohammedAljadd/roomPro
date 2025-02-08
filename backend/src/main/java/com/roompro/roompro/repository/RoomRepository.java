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

    @Query("SELECT r FROM Room r " +
            "LEFT JOIN r.roomEquipmentMappings rem " +
            "LEFT JOIN rem.equipment e " +
            "WHERE (:capacity IS NULL OR r.capacity >= :capacity) " + // Capacity filter
            "AND (:location IS NULL OR r.location LIKE %:location%) " + // Location filter
            "AND (:equipmentName IS NULL OR e.name LIKE %:equipmentName%)") // Equipment filter
    List<Room> findAllWithFilters(int capacity, String location, String equipmentName);

}
