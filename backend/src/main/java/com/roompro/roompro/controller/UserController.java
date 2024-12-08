package com.roompro.roompro.controller;

import com.roompro.roompro.dto.UserRegistrationDto;
import com.roompro.roompro.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/roompro/users")
public class UserController {
    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {

        registrationService.registerUser(userRegistrationDto);
        return ResponseEntity.ok("User registered successfully");
    }

}
