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
@Table(name = "cleaning_types")
public class CleaningType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cleaningId;

    private String typeName;

    private String description;
}
