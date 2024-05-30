package com.ojtportal.api.dto;

import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainingPlanDTO {
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
