package com.roompro.roompro.repository;

import com.roompro.roompro.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByEmail(String email);
}