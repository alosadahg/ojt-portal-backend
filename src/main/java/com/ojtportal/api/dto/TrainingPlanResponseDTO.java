package com.ojtportal.api.dto;

import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainingPlanResponseDTO {
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalTasks;
    private int completedTasks;
}
