package com.roompro.roompro.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    private String name;

    private int capacity;

    private String location;

    private String description;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Booking> bookings;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    
    private List<RoomEquipmentMapping> roomEquipmentMappings;


    @Override
    public String toString() {
        return "Room{" +
                "roomId=" + roomId +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", bookings=" + (bookings != null ? bookings.size() : 0) +  // Avoiding recursion and printing the size of bookings list
                ", roomEquipmentMappings=" + (roomEquipmentMappings != null ? roomEquipmentMappings.size() : 0) +  // Avoiding recursion and printing the size of roomEquipmentMappings list
                '}';
    }


}
