package com.roompro.roompro.service;

import com.roompro.roompro.dto.UserRegistrationDto;
import com.roompro.roompro.model.Role;
import com.roompro.roompro.model.Users;
import com.roompro.roompro.repository.RoleRepository;
import com.roompro.roompro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Users registerUser(UserRegistrationDto userRegistrationDto) {
        // Check if user already exists
        String email = userRegistrationDto.getEmail();

        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("User already exists with this email");
        }


        String roleName = userRegistrationDto.getRoleName();
        Role role = roleRepository.findByName(roleName).orElse(null);

        if (role == null) {
            throw new RuntimeException("Invalid role");
        }

        // Create new user
        Users user = new Users();
        user.setEmail(email);
        user.setPassword(encoder.encode(userRegistrationDto.getPassword()));
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setRole(role);

        // Save user to the database
        return userRepository.save(user);
    }
}
