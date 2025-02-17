package com.roompro.roompro.service.mapper;

import com.roompro.roompro.dto.response.RoomResponseDTO;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.model.RoomEquipmentMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    @Mapping(source = "roomEquipmentMappings", target = "equipments")
    RoomResponseDTO roomToRoomResponseDTO(Room room);

    default List<String> mapRoomEquipmentMappingsToEquipments(List<RoomEquipmentMapping> roomEquipmentMappings) {
        if (roomEquipmentMappings == null) {
            return null;
        }
        return roomEquipmentMappings.stream()
                .map(roomEquipmentMapping -> roomEquipmentMapping.getEquipment().getName()) // Assuming Equipment has a `getName()` method
                .collect(Collectors.toList());
    }
}
