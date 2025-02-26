package com.roompro.roompro.service;


import com.roompro.roompro.dto.request.UserRegistrationRequestDTO;
import com.roompro.roompro.dto.request.UserRequestDTO;
import com.roompro.roompro.model.Role;
import com.roompro.roompro.model.Users;
import com.roompro.roompro.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserServiceIT {


    @Autowired
    LoginService loginService;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JWTService jwtService;

    @Autowired
    ApplicationContext context;

    private Role role;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    void setUp(){
        // Setup the dabatase

        role =  new Role(1L, "Admin", "desc", Collections.emptyList());

        Users user1 = new Users();
        user1.setEmail("email1@gmail.com");
        user1.setPassword(encoder.encode("pass1"));
        user1.setFirstName("fname1");
        user1.setLastName("lname1");
        user1.setRole(role);

        Users user2 = new Users();
        user2.setEmail("email2@gmail.com");
        user2.setPassword(encoder.encode("pass2"));
        user2.setFirstName("fname2");
        user2.setLastName("lname2");
        user2.setRole(role);

        Users user3 = new Users();
        user3.setEmail("email3@gmail.com");
        user3.setPassword(encoder.encode("pass3"));
        user3.setFirstName("fname3");
        user3.setLastName("lname3");
        user3.setRole(role);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
    }


    // --------------------- Tests for registration ---------------------

    @Test
    @Transactional // changes removed from db after tests done
    void shouldCreateUserSuccessfully() throws Exception {

        // Save user in DB
        UserRegistrationRequestDTO userDTO =
                new UserRegistrationRequestDTO("test@gmail.com", "pass", "testFName", "testLName");
        registrationService.registerUser(userDTO);


        // Check if user registered
        Users user =  userRepository.findByEmail("test@gmail.com");

        assertEquals(userDTO.getEmail(), user.getEmail());
        assertTrue(encoder.matches(userDTO.getPassword(), user.getPassword())); // BCrypt is designed to generate a different hash each time
        assertEquals(userDTO.getFirstName(), user.getFirstName());
        assertEquals(userDTO.getLastName(), user.getLastName());

    }


    @Test
    @Transactional
    void shoudNotRegisterIfEmailAlreayExist() throws Exception {
        UserRegistrationRequestDTO userDTO =
                new UserRegistrationRequestDTO("email1@gmail.com", "pass", "testFName", "testLName");

        Exception exception = assertThrows(Exception.class, () -> registrationService.registerUser(userDTO));
        assertEquals("Invalid registration details: User already exists with this email", exception.getMessage());
    }


    // --------------------- Tests for login ---------------------

    @Test
    @Transactional
    void shouldLoginUserSuccessfully() throws Exception{
        UserRequestDTO userDTO = new UserRequestDTO("email1@gmail.com", "pass1");

        Optional<String> token = loginService.authenticate(userDTO);

        assertTrue(token.isPresent());
    }

    @Test
    @Transactional
    void shouldReturnValidTokenSuccessfully() throws Exception{

        String email = "email1@gmail.com";

        UserRequestDTO userDTO = new UserRequestDTO(email, "pass1");

        Optional<String> token = loginService.authenticate(userDTO);
        UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(email);

        assertTrue(token.isPresent());
        assertTrue(jwtService.validateToken(token.get(), userDetails));


    }


}