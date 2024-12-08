package com.roompro.roompro.controller;

import com.roompro.roompro.dto.BookingDto;
import com.roompro.roompro.dto.UserLoginDto;
import com.roompro.roompro.dto.UserRegistrationDto;
import com.roompro.roompro.service.BookingService;
import com.roompro.roompro.service.LoginService;
import com.roompro.roompro.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/roompro/users")
public class UserController {
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private BookingService bookingService;

    // Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {

        registrationService.registerUser(userRegistrationDto);
        return ResponseEntity.ok("User registered successfully");
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserLoginDto userLoginDto) {

        boolean isAuthenticated = loginService.loginUser(userLoginDto);

        if(isAuthenticated){
            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(401).body("Invalid email or password");
    }

    @PostMapping("/booking")
    public ResponseEntity<String> createBooking(@RequestBody BookingDto bookingDto) {
        bookingService.createBooking(bookingDto);
        return ResponseEntity.ok("Booking successfully");
    }
}
