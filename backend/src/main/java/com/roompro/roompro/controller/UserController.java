package com.roompro.roompro.controller;

import com.roompro.roompro.dto.request.UserRegistrationRequestDTO;
import com.roompro.roompro.dto.request.UserRequestDTO;
import com.roompro.roompro.dto.response.BookingTrendsResponseDTO;
import com.roompro.roompro.dto.response.UsersStatsResponseDTO;
import com.roompro.roompro.service.LoginService;
import com.roompro.roompro.service.RegistrationService;
import com.roompro.roompro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/roompro")
@CrossOrigin(origins = "http://localhost:4209")
public class UserController {
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;


    // Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequestDTO userRegistrationDto) {

        if (userRegistrationDto.getEmail() == null || userRegistrationDto.getEmail().isEmpty() ||
            userRegistrationDto.getPassword() == null || userRegistrationDto.getPassword().isEmpty() ||
            userRegistrationDto.getFirstName() == null || userRegistrationDto.getFirstName().isEmpty() ||
            userRegistrationDto.getLastName() == null || userRegistrationDto.getLastName().isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All fields are required.");
        }

        // Check if email is valid
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(userRegistrationDto.getEmail());
        if (!matcher.matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please enter a valid email address.");
        }

        try {

            registrationService.registerUser(userRegistrationDto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDTO userRequestDTO) {

        if (userRequestDTO.getEmail() == null || userRequestDTO.getEmail().isEmpty() ||
                userRequestDTO.getPassword() == null || userRequestDTO.getPassword().isEmpty()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All fields are required.");
        }

        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(userRequestDTO.getEmail());
        if (!matcher.matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please enter a valid email address.");
        }

        Optional<String> token = loginService.authenticate(userRequestDTO);
        if(!token.isPresent()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
        Map<String, String> response = Map.of("token", token.get());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/stats/{year}/{month}")
    public UsersStatsResponseDTO getUserStats(@PathVariable int year, @PathVariable int month){
        UsersStatsResponseDTO stats = userService.getUserStats(year, month);
        return stats;
    }

}
