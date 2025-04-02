package com.roompro.roompro.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;


import java.time.LocalTime;

@Data
@AllArgsConstructor
public class CleaningWeeklyResponseDTO {
    private long roomId;
    private String starttime;
    private String endtime;
    private String cleaningDay;
    private String setDate;
}
