package com.ojtportal.api.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ojtportal.api.model.AccountStatus;
import com.ojtportal.api.model.Role;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SupervisorFullDTO {
    private UserDTO userInfo;
    private String company_email;
    private String company_contactNo;
    private String company_location;
    private String company_name;
    private String position;
    private String accessToken;
    private Role accountType;
    private AccountStatus userStatus;

    public SupervisorFullDTO(UserDTO userInfo, String company_email, String company_contactNo, String company_location,
            String company_name, String position) {
        this.userInfo = userInfo;
        this.company_email = company_email;
        this.company_contactNo = company_contactNo;
        this.company_location = company_location;
        this.company_name = company_name;
        this.position = position;
    }

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
