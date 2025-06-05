package com.roompro.roompro.service;


import com.roompro.roompro.dto.request.BookingRequestDTO;
import com.roompro.roompro.dto.request.UserRequestDTO;
import com.roompro.roompro.model.Users;
import com.roompro.roompro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JWTService jwtService;

    public Optional<String> authenticate(UserRequestDTO user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            // If authentication succeeds, generate the token
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(user.getEmail());

                // increase login count, update last login
                Users LoggedInUser = userRepository.findByEmail(user.getEmail());
                LoggedInUser.setLoginCount(LoggedInUser.getLoginCount()+1);

                LoggedInUser.setLastLoginAt(LocalDateTime.now());
                
                userRepository.save(LoggedInUser);

                return Optional.of(token);
            }
        } catch (AuthenticationException e) {  // Catches ALL authentication failures
            return Optional.empty();
        }

        return Optional.empty();
    }


}
