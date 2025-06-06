package com.roompro.roompro.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersStatsResponseDTO {
    private int totalUser;
    private double averageLoginsPerUser;
    private Map<String, Integer> loginCountsByName;
    private int totalLoginTheCurrentMonth;
    private Map<String, Integer> userWithMostLogging;
    private Map<String, Integer> userWithLeastLogging;
    private Map<Integer, Integer> mostLoggedHour;
}
