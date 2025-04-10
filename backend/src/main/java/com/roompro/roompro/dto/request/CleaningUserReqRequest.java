package com.roompro.roompro.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CleaningUserReqRequest {
    private long roomId;
    private String message;
    private LocalDateTime requestedAt;

}
