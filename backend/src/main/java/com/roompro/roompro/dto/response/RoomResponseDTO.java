package com.roompro.roompro.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RoomResponseDTO {
    private Long  roomId;
    private String name;
    private Short capacity;
    private String location;
    private String description;
    private List<String> equipments;

}
