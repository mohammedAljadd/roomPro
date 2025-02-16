package com.roompro.roompro.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private Users user;

    @ManyToOne

    @JoinColumn(name = "room_id", nullable = false)

    private Room room;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", user=" + (user != null ? user.getUserId() : null) +  // Assuming Users class has getId() method
                ", room=" + (room != null ? room.getRoomId() : null) +  // Assuming Room class has getRoomId() method
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

}
