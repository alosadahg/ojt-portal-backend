package com.ojtportal.api.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OJTRecordDTO {
    private String designation;
    private String department;
    private double ojtHours;
    private String studentEmail;
    public OJTRecordDTO(String designation, String department, double ojtHours) {
        this.designation = designation;
        this.department = department;
        this.ojtHours = ojtHours;
    }

    
}
