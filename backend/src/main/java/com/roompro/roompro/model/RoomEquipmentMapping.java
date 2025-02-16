package com.roompro.roompro.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "room_equipment_mapping")
public class RoomEquipmentMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnore
    private Room room;

    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Override
    public String toString() {
        return "RoomEquipmentMapping{" +
                "id=" + id +
                ", room=" + (room != null ? room.getRoomId() : null) +  // Avoiding recursion by printing only roomId
                ", equipment=" + (equipment != null ? equipment.getEquipmentId() : null) +  // Avoiding recursion by printing only equipmentId
                '}';
    }


}
