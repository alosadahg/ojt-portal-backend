package com.ojtportal.api.dto;

import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainingPlanDTO {
    private int plan_id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    public TrainingPlanDTO(String title, String description, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
