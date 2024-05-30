package com.ojtportal.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ojtportal.api.model.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentFullDTO {
    private StudentDTO userInfo;
    private SupervisorDTO supervisorInfo;
    private List<TrainingPlanResponseDTO> trainingPlans = new ArrayList<TrainingPlanResponseDTO>();
    private OJTRecordResponseDTO ojtRecord;
    private List<TaskListDTO> tasksByTrainingPlan = new ArrayList<TaskListDTO>();
    private String accessToken;
    private Role accountType;

    @Override
    public String toString() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            return objectWriter.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "ERROR: Failed to convert object to JSON. Details: " + e.getMessage();
        }
    }

}
