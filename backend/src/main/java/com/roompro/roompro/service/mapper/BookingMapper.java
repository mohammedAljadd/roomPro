package com.roompro.roompro.service.mapper;


import com.roompro.roompro.dto.response.BookingResponseDTO;
import com.roompro.roompro.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = RoomMapper.class)
public interface BookingMapper {


    @Mapping(source = "room", target = "room")
    @Mapping(source = "user.email", target = "userEmail")
    BookingResponseDTO bookingToBookingResponseDTO(Booking booking);


}
