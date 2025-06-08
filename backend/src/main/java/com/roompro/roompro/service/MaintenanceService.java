package com.roompro.roompro.service;


import com.roompro.roompro.dto.response.MaintenanceResponseDTO;
import com.roompro.roompro.dto.response.RoomCleaningResponseDTO;
import com.roompro.roompro.dto.response.RoomMaintenanceResponseDTO;
import com.roompro.roompro.dto.response.RoomResponseDTO;
import com.roompro.roompro.model.Maintenance;
import com.roompro.roompro.model.Room;
import com.roompro.roompro.repository.BookingRepository;
import com.roompro.roompro.repository.MaintenanceRepository;
import com.roompro.roompro.repository.RoomRepository;
import com.roompro.roompro.service.mapper.RoomMapper;
import com.roompro.roompro.service.mapper.RoomMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MaintenanceService {

    @Autowired
    MaintenanceRepository maintenanceRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    BookingRepository bookingRepository;

    private RoomMapper roomMapper = new RoomMapperImpl();

    public List<Maintenance> findMaintenanceSlotsByRoom(Long roomId){
        return maintenanceRepository.findByRoom_RoomId(roomId);
    }

    public List<RoomMaintenanceResponseDTO> findAll(){

        List<Object []> rms = roomRepository.findRoomsWithMaintenance();
        List<RoomMaintenanceResponseDTO> roomMaintenanceResponseDTOS = new ArrayList<>(); ;
        for (Object[] row : rms) {
            Room room = (Room) row[0];
            RoomResponseDTO rr = roomMapper.roomToRoomResponseDTO(room);

            Maintenance m = (Maintenance) row[1];

            // skip expired or cancelled maintenance periods
            boolean isExpired = m!=null && m.getEndDate().isBefore(LocalDateTime.now());
            boolean isCanceled = m!=null && m.isCanceled();
            if (isExpired || isCanceled) {
                m = null;
            }



            RoomMaintenanceResponseDTO rm = new RoomMaintenanceResponseDTO();
            rm.setRoomDetails(rr);
            if(m!=null){
                rm.setMaintenances(new MaintenanceResponseDTO(m.getMaintenanceId(), m.getRoom().getRoomId(),
                        m.getStartDate().toString(), m.getEndDate().toString()));
            }
            else{
                rm.setMaintenances(null);
            }

            roomMaintenanceResponseDTOS.add(rm);
        }
        return roomMaintenanceResponseDTOS;
    }


    public Map<String, String> cancelMaintenance(Long id) throws Exception {
        if( maintenanceRepository.findById(id).isEmpty()){
            throw new Exception("Maintenance slot not found.");
        }

        Maintenance maintenance = maintenanceRepository.findById(id).get();
        maintenance.setCanceled(true);
        maintenanceRepository.save(maintenance);

        return Map.of("message", "The maintenance schedule has been canceled successfully.");
    }



    boolean isOverlappingWithMaintenanceSlots(long roomId, LocalDateTime startDate, LocalDateTime endDate){
        return maintenanceRepository.isOverlappingWithMaintenanceSlots(roomId, startDate, endDate);
    }


    public void createMaintenance(MaintenanceResponseDTO maintenanceDTO) throws Exception {
        LocalTime businessStart = LocalTime.of(8, 0);  // 8:00 AM
        LocalTime businessEnd = LocalTime.of(18, 0);

        LocalDateTime startDateTime = LocalDateTime.parse(maintenanceDTO.getStartDate());
        LocalDateTime endDateTime = LocalDateTime.parse(maintenanceDTO.getEndDate());

        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new Exception("Start date and time must be in the future.");
        }

        if (startDateTime.isAfter(endDateTime)) {
            throw new Exception("End date must be after the start date.");
        }


        if (startDateTime.toLocalTime().isBefore(businessStart) ||
                endDateTime.toLocalTime().isAfter(businessEnd) ||
                startDateTime.toLocalTime().isAfter(businessEnd) ||
                endDateTime.toLocalTime().isBefore(businessStart)) {

            throw new Exception("Maintenance must be scheduled between 08:00 AM and 06:00 PM.");
        }

        if(startDateTime.getDayOfWeek()== DayOfWeek.SATURDAY || startDateTime.getDayOfWeek()==DayOfWeek.SUNDAY){
            throw new Exception("Maintenance must be scheduled from Monday through Friday. Please choose a weekday to continue.");
        }


        // Check if maintenance slot overlap with a booked slot
        if(bookingRepository.isOverlappingWithOtherBookings(maintenanceDTO.getRoomId(),
                startDateTime, endDateTime)){
            throw new Exception("Maintenance slot overlap with a booked slot.");
        }

        Maintenance maintenance = new Maintenance();
        maintenance.setRoom(roomRepository.findById(maintenanceDTO.getRoomId()).get());
        maintenance.setStartDate(startDateTime);
        maintenance.setEndDate(endDateTime);
        maintenanceRepository.save(maintenance);
    }


}
