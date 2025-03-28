package com.roompro.roompro.controller;

import com.roompro.roompro.dto.response.BookingResponseDTO;
import com.roompro.roompro.dto.response.CleaningAfterUseResponseDTO;
import com.roompro.roompro.model.Booking;
import com.roompro.roompro.model.CleaningAfterUse;
import com.roompro.roompro.service.CleaningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roompro")
@CrossOrigin(origins = "http://localhost:4209")
public class CleaningController {

    @Autowired
    CleaningService cleaningService;


    @GetMapping("/cleaning/after-use")
    public List<CleaningAfterUseResponseDTO> getAllCleaningAfterUse(){

        List<CleaningAfterUse> cs = cleaningService.getAll();

        return cs.stream().map(c->new CleaningAfterUseResponseDTO(c.getStartTime(), c.getEndTime())).toList();
    }

}
