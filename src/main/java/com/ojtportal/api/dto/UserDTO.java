package com.ojtportal.api.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class UserDTO {
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String department;
    public UserDTO(String email, String firstname, String lastname) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    
}
