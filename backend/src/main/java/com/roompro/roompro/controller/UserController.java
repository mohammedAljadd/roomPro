package com.roompro.roompro.controller;

import com.roompro.roompro.dto.BookingDto;
import com.roompro.roompro.dto.UserLoginDto;
import com.roompro.roompro.dto.UserRegistrationDto;
import com.roompro.roompro.service.BookingService;
import com.roompro.roompro.service.LoginService;
import com.roompro.roompro.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/roompro")
@CrossOrigin(origins = "http://localhost:4209")
public class UserController {
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private BookingService bookingService;

    // Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {

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
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserLoginDto userLoginDto) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean isAuthenticated = loginService.loginUser(userLoginDto);

            if (isAuthenticated) {
                response.put("message", "User logged in successfully");
                return ResponseEntity.ok(response);
            }

            response.put("error", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/booking")
    public ResponseEntity<String> createBooking(@RequestBody BookingDto bookingDto) {
        bookingService.createBooking(bookingDto);
        return ResponseEntity.ok("Booking successfully");
    }
}
