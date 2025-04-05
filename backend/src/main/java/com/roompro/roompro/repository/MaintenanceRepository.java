package com.roompro.roompro.repository;

import com.roompro.roompro.model.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    List<Maintenance> findByRoom_RoomId(Long roomId);

    Optional<Maintenance> findById(Long id);

    @Query("SELECT COUNT(m) > 0 FROM Maintenance m WHERE m.room.id = :roomId AND " +
            "m.startDate < :endDate AND m.endDate > :startDate")
    boolean isOverlappingWithMaintenanceSlots(long roomId, LocalDateTime startDate, LocalDateTime endDate);
}
