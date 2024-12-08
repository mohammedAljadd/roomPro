package com.roompro.roompro.service;

import com.roompro.roompro.dto.UserRegistrationDto;
import com.roompro.roompro.model.Role;
import com.roompro.roompro.model.User;
import com.roompro.roompro.repository.RoleRepository;
import com.roompro.roompro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public User registerUser(UserRegistrationDto userRegistrationDto) {
        // Check if user already exists
        String email = userRegistrationDto.getEmail();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User already exists with this email");
        }


        String roleName = userRegistrationDto.getRoleName();
        Role role = roleRepository.findByName(roleName).orElse(null);

        if (role == null) {
            throw new RuntimeException("Invalid role");
        }

        // Create new user
        User user = new User();
        user.setEmail(email);
        user.setPassword(user.getPassword());
        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setRole(role);

        // Save user to the database
        return userRepository.save(user);
    }
}
