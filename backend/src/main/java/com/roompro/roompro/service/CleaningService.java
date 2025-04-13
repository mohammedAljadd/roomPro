package com.roompro.roompro.service;


import com.roompro.roompro.dto.request.CleaningAdminSetStatusRequestDTO;
import com.roompro.roompro.dto.request.CleaningUserReqRequest;
import com.roompro.roompro.dto.response.CleaningOnRequestResponseDTO;
import com.roompro.roompro.enums.CleaningStatus;
import com.roompro.roompro.model.*;
import com.roompro.roompro.repository.*;
import com.roompro.roompro.service.mapper.RoomMapper;
import com.roompro.roompro.service.mapper.RoomMapperImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CleaningService {

    @Autowired
    CleaningAssignmentRepository cleaningRepository;

    @Autowired
    AfterUseCleaningRepository afterUseCleaningRepository;

    @Autowired
    CleaningWeeklyRepository cleaningWeeklyRepository;

    @Autowired
    CleaningUserRequestRepository cleaningUserRequestRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoomRepository roomRepository;

    private RoomMapper roomMapper = new RoomMapperImpl();


    public List<CleaningAfterUse> getAllAfterUseCleaning(long roomId){
        return afterUseCleaningRepository.findByRoomId(roomId);
    }




    public List<CleaningWeekly> getAllWeeklyCleaning(long roomId){
        return cleaningWeeklyRepository.findByRoomId(roomId);
    }


    public boolean checkIfNeedCleaningAfterUse(long roomId){
       List<RoomCleaningAssignment> result = cleaningRepository.checkIfNeedCleaningAfterUse(roomId);
       return !result.isEmpty();
    }


    public void setCleaningSlot(LocalDateTime endDateTime, Room room){
        CleaningAfterUse cleaningAfterUse = new CleaningAfterUse();
        cleaningAfterUse.setRoom(room);
        cleaningAfterUse.setStartTime(endDateTime);
        cleaningAfterUse.setEndTime(endDateTime.plusMinutes(20)); // cleaning for 20 minutes

        afterUseCleaningRepository.save(cleaningAfterUse);

    }

    public void deleteAfterUseCleaningSlots(long roomId){
        afterUseCleaningRepository.deleteByRoom_RoomId(roomId);
    }

    public void userRequestCleaning(CleaningUserReqRequest cleaningRequest){

        CleaningOnRequest cleaningOnRequest = new CleaningOnRequest();
        cleaningOnRequest.setRequestedAt(cleaningRequest.getRequestedAt());

        // Set user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users user = userRepository.findByEmail(email);
        cleaningOnRequest.setUser(user);

        // Set room
        Room room = roomRepository.findById(cleaningRequest.getRoomId()).get();
        cleaningOnRequest.setRoom(room);

        // Set status
        cleaningOnRequest.setStatus(CleaningStatus.ON_HOLD);

        // Set message
        cleaningOnRequest.setMessage(cleaningRequest.getMessage());

        cleaningOnRequest.setViewedByUser(false);

        cleaningUserRequestRepository.save(cleaningOnRequest);
    }

    public void markCleaningRequestAsViewed(Long cleaningID){
        CleaningOnRequest cleaningRequest = cleaningUserRequestRepository.findById(cleaningID).get();
        cleaningRequest.setViewedByUser(true);
        cleaningUserRequestRepository.save(cleaningRequest);
    }

    public List<CleaningOnRequestResponseDTO> getCleaningRequests(boolean showProcessed) {
        List<CleaningOnRequest> cleaningRequests = cleaningUserRequestRepository.findAllRequests();

        if(showProcessed){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            Users user = userRepository.findByEmail(email);
            cleaningRequests  = cleaningRequests.stream().filter(request->(request.getStatus()!=CleaningStatus.ON_HOLD &&
                    request.getUser().equals(user) && !request.isViewedByUser()
            )).toList();
        }

        List<CleaningOnRequestResponseDTO> cRequestsDTO = cleaningRequests.stream().map(
                request -> {
                    CleaningOnRequestResponseDTO dto = new CleaningOnRequestResponseDTO();
                    dto.setCleaningId(request.getCleaningId());
                    dto.setRequestedAt(request.getRequestedAt());
                    dto.setStatus(request.getStatus());
                    Room room = request.getRoom();
                    dto.setRoom(roomMapper.roomToRoomResponseDTO(room));
                    dto.setMessage(request.getMessage());
                    dto.setUserFirstName(request.getUser().getFirstName());
                    dto.setUserLastName(request.getUser().getLastName());
                    dto.setStartTime(request.getStartTime());
                    dto.setEndTime(request.getEndTime());

                    return dto;

                }).collect(Collectors.toList());

        return cRequestsDTO;
    }

    public void setCleaningRequestStatus(CleaningAdminSetStatusRequestDTO cleaningSetStatus){

        long cleaningId = cleaningSetStatus.getCleaningId();
        String status = cleaningSetStatus.getStatus();

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        CleaningOnRequest cleaningOnRequest = cleaningUserRequestRepository.findById(cleaningId).get();
        if(!cleaningSetStatus.getStatus().equals("REJECTED")){
            startTime = LocalDateTime.parse(cleaningSetStatus.getStartTime());
            endTime = LocalDateTime.parse(cleaningSetStatus.getEndTime());
            cleaningOnRequest.setStartTime(startTime);
            cleaningOnRequest.setEndTime(endTime);
        }






        cleaningOnRequest.setStatus(CleaningStatus.valueOf(status));


        cleaningUserRequestRepository.save(cleaningOnRequest);

    }
}
