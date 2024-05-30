package com.ojtportal.api.dto;

import java.time.LocalDateTime;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class LogbookDTO {
    private int entryID;
    private LocalDateTime timeIn;
    private LocalDateTime timeOut;
    private String activities;
    public LogbookDTO(LocalDateTime timeIn, LocalDateTime timeOut, String activities) {
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.activities = activities;
    }
    public LogbookDTO(int entryID, String activities) {
        this.entryID = entryID;
        this.activities = activities;
    }    
}
