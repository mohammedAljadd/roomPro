package com.roompro.roompro.controller;

import com.roompro.roompro.dto.request.CleaningAdminSetStatusRequestDTO;
import com.roompro.roompro.dto.request.CleaningUserReqRequest;
import com.roompro.roompro.dto.response.BookingResponseDTO;
import com.roompro.roompro.dto.response.CleaningAfterUseResponseDTO;
import com.roompro.roompro.dto.response.CleaningOnRequestResponseDTO;
import com.roompro.roompro.dto.response.CleaningWeeklyResponseDTO;
import com.roompro.roompro.model.Booking;
import com.roompro.roompro.model.CleaningAfterUse;
import com.roompro.roompro.model.CleaningWeekly;
import com.roompro.roompro.service.CleaningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roompro")
@CrossOrigin(origins = "http://localhost:4209")
public class CleaningController {

    @Autowired
    CleaningService cleaningService;



    @GetMapping("/cleaning/after-use")
    public List<CleaningAfterUseResponseDTO> getAllCleaningAfterUse(
            @RequestParam(required = true) Long roomId){

        List<CleaningAfterUse> cs = cleaningService.getAllAfterUseCleaning(roomId);
        return cs.stream().map(c->new CleaningAfterUseResponseDTO(c.getCleaningId(), c.getStartTime(), c.getEndTime())).toList();
    }

    @GetMapping("/cleaning/weekly")
    public CleaningWeeklyResponseDTO getAllWeeklyCleaning(
            @RequestParam(required = true) Long roomId){

        List<CleaningWeekly> cs = cleaningService.getAllWeeklyCleaning(roomId);

        if(cs.isEmpty()){
            return null;
        }
        return cs.stream().map(c->new CleaningWeeklyResponseDTO(c.getCleaningId(), c.getStarttime().toString(), c.getEndtime().toString(), c.getCleaningDay(), c.getSetDate().toString())).toList().getFirst();
    }


    @PostMapping("cleaning/request")
    public ResponseEntity<?> userRequestCleaning(@RequestBody CleaningUserReqRequest cleanRequest){

        cleaningService.userRequestCleaning(cleanRequest);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Your cleaning request has been submitted and is currently pending approval. Thank you for keeping the workspace clean!");
        return ResponseEntity.ok(response);

    }

    @GetMapping("cleaning/request/get")
    public List<CleaningOnRequestResponseDTO> getCleaningRequests(){
        return cleaningService.getCleaningRequests(false);
    }

    @GetMapping("cleaning/request/get/processed")
    public List<CleaningOnRequestResponseDTO> getProcessedCleaningRequests(){

        return cleaningService.getCleaningRequests(true);
    }

    @PatchMapping("cleaning/request/processed/marked-view/{cleaning_id}")
    public void markProcessedCleaningRequestsAsViewed(
            @PathVariable Long cleaning_id
    ){
        cleaningService.markCleaningRequestAsViewed(cleaning_id);
    }

    @PatchMapping("cleaning/request/set_status")
    public ResponseEntity<?> setCleaningRequestStatus(@RequestBody CleaningAdminSetStatusRequestDTO cleaningSetStatus){

        cleaningService.setCleaningRequestStatus(cleaningSetStatus);

        System.out.println(cleaningSetStatus.getStatus());

        Map<String, String> response = new HashMap<>();
        String message = "The cleaning request got "+cleaningSetStatus.getStatus().toLowerCase()+" successfully!";
        response.put("message", message);
        return ResponseEntity.ok(response);

    }


}
