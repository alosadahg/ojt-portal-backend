package com.ojtportal.api.dto;

import java.util.List;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AssignTrainingPlanDTO {
    private int trainingPlanID;
    private List<String> internEmails;
}
