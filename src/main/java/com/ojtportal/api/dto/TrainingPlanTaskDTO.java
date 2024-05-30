package com.ojtportal.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrainingPlanTaskDTO {
    private int trainingPlanID;
    private TaskDTO task;
    private List<SkillDTO> skills;
}
