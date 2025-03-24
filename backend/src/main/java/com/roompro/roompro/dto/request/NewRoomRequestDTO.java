package com.roompro.roompro.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewRoomRequestDTO {

    private String roomName;
    private String location;
    private String description;
    private short capacity;
    List<Long> equipmentsIDs;
}
