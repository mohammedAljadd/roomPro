package com.roompro.roompro.controller;

import com.roompro.roompro.dto.request.BookingRequestDTO;
import com.roompro.roompro.dto.request.UserRegistrationRequestDTO;
import com.roompro.roompro.dto.request.UserRequestDTO;
import com.roompro.roompro.service.LoginService;
import com.roompro.roompro.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/roompro")
@CrossOrigin(origins = "http://localhost:4209")
public class UserController {
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private LoginService loginService;


    // Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserRegistrationRequestDTO userRegistrationDto) {

        try {

            registrationService.registerUser(userRegistrationDto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDTO user) {

        Optional<String> token = loginService.authenticate(user);
        if(!token.isPresent()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
        Map<String, String> response = Map.of("token", token.toString());
        return ResponseEntity.ok(response);
    }

}
