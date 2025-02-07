package com.roompro.roompro.service;


import com.roompro.roompro.dto.UserLoginDto;
import com.roompro.roompro.model.Users;
import com.roompro.roompro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JWTService jwtService;

    public Map<String, String> verify(UserLoginDto user) {

        // Attempt to authenticate the user with the provided username and password
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        // If authentication is successful, generate the token and return it as JSON
        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(user.getEmail());
            return Map.of("token", token);  // Return token as JSON response
        }

        // If authentication fails, return an error message as JSON
        return Map.of("message", "Invalid credentials");
    }

}
