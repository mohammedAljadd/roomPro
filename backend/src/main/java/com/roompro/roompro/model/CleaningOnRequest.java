package com.roompro.roompro.model;

import com.roompro.roompro.enums.CleaningStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cleaning_on_request")
public class CleaningOnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cleaningId;


    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "start_time", nullable = true)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = true)
    private LocalDateTime endTime;

    @Column(name = "requested_at", nullable = false)
    private  LocalDateTime requestedAt;

    @Enumerated(EnumType.STRING)
    private CleaningStatus status;

    @Column(length = 500)
    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    private boolean isViewedByUser;

}
