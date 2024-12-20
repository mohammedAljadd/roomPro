package com.roompro.roompro.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String roleName;

}
