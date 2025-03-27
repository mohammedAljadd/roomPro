package com.roompro.roompro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "room_cleaning_assignments")
public class RoomCleaningAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "roomId")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "cleaning_type_id", referencedColumnName = "cleaningId")
    private CleaningType cleaningType;
}