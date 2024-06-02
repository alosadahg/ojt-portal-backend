package com.ojtportal.api.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SkillTrendResponseDTO {
    private String skill;
    
    @JsonProperty("startDate")
    private LocalDate startDate;
    
    @JsonProperty("endDate")
    private LocalDate endDate;
    
    @JsonProperty("avg_demand_change")
    private double avgDemandChange;
    
    private String trend;
    
    @JsonProperty("yearly_data")
    private List<SkillResponseDTO> yearlyData;

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
