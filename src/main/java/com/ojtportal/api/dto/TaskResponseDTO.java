package com.ojtportal.api.dto;

import com.ojtportal.api.model.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TaskResponseDTO {
    private String title;
    private String description;
    private String objective;
    private TaskStatus status;
}
