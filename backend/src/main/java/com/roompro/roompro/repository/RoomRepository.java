package com.roompro.roompro.repository;

import com.roompro.roompro.model.Room;
import com.roompro.roompro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findById(Long roomId);
}
