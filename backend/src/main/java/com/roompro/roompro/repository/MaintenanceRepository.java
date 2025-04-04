package com.roompro.roompro.repository;

import com.roompro.roompro.model.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    List<Maintenance> findByRoom_RoomId(Long roomId);

    @Query("SELECT COUNT(m) > 0 FROM Maintenance m WHERE m.room.id = :roomId AND " +
            "(m.startTime < :endTime AND m.endTime > :startTime) " +
            "AND m.startDate < :endDate AND m.endDate > :startDate")
    boolean isOverlappingWithMaintenanceSlots(long roomId, LocalDateTime startDate, LocalDateTime endDate, LocalTime startTime, LocalTime endTime);
}
