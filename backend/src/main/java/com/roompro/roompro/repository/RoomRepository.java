package com.roompro.roompro.repository;

import com.roompro.roompro.model.Room;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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


    @Query("select r, ct.typeName as cleaningType, ct.description as cleaning_description, cw.cleaningDay " +
            "from Room r " +
            "join RoomCleaningAssignment rca on rca.room=r " +
            "join CleaningType ct on ct=rca.cleaningType " +
            "left join CleaningWeekly cw on cw.room=r")
    List<Object []> findRoomsWithCleaningType();

    @Modifying // for update
    @Transactional
    @Query("update  RoomCleaningAssignment r set r.cleaningType.cleaningId=:cleaningId where r.room.roomId=:roomId")
    void updateCleaningType(long roomId, long cleaningId);
}
