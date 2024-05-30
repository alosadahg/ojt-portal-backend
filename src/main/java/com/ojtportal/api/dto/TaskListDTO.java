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
public class TaskListDTO {
    private String trainingPlanTitle;
    private List<TaskResponseDTO> tasks;
}
