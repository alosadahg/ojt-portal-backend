package com.ojtportal.api.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SupervisorDTO extends UserDTO {
    private String company_email;
    private String company_contactNo;
    private String company_location;
    private String company_name;
    private String position;

    public SupervisorDTO(String email, String firstname, String lastname, String company_email,
            String company_contactNo, String company_location, String company_name, String position) {
        super(email, firstname, lastname);
        this.company_email = company_email;
        this.company_contactNo = company_contactNo;
        this.company_location = company_location;
        this.company_name = company_name;
        this.position = position;
    }

    
    
}
