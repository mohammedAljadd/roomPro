package com.roompro.roompro.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequestDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String roleName;

}
