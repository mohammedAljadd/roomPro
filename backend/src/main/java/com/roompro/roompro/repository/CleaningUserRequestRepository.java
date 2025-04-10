package com.roompro.roompro.repository;

import com.roompro.roompro.model.CleaningOnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CleaningUserRequestRepository extends JpaRepository<CleaningOnRequest, Long> {

    @Query(value = "select * from cleaning_on_request where status='ON_HOLD'", nativeQuery = true)
    List<CleaningOnRequest> findOnHoldRequests();

}
