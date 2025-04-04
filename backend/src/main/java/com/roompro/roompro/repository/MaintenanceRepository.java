package com.roompro.roompro.repository;

import com.roompro.roompro.model.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    List<Maintenance> findByRoom_RoomId(Long roomId);
}
