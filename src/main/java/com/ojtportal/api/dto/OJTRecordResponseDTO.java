package com.ojtportal.api.dto;

import com.ojtportal.api.model.OJTStatus;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OJTRecordResponseDTO {
    private String designation;
    private String department;
    private double ojtHours;
    private double renderedHrs;   
    private OJTStatus status;
}
