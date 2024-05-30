package com.ojtportal.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentDTO extends UserDTO {
    private String studentID;
    private String degreeProgram;
    public StudentDTO(String email, String firstname, String lastname, String studentID, String degreeProgram) {
        super(email, firstname, lastname);
        this.studentID = studentID;
        this.degreeProgram =degreeProgram;
    }
}
