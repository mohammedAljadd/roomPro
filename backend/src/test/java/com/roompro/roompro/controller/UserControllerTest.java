package com.roompro.roompro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roompro.roompro.config.TestSecurityConfig;
import com.roompro.roompro.dto.request.UserRegistrationRequestDTO;
import com.roompro.roompro.dto.request.UserRequestDTO;
import com.roompro.roompro.service.JWTService;
import com.roompro.roompro.service.LoginService;
import com.roompro.roompro.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private RegistrationService registrationService;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private JWTService jwtService;



    @Test
    void shouldNotRegisterUserIfNullFields() throws Exception {

        UserRegistrationRequestDTO userDTO =
                new UserRegistrationRequestDTO(null, null, null, null);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(userDTO);


        doNothing().when(registrationService).registerUser(userDTO);
        mockMvc.perform(post("/roompro/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("All fields are required."));


    }

    @Test
    void shouldNotRegisterUserIfInvalidEmail() throws Exception {

        UserRegistrationRequestDTO userDTO =
                new UserRegistrationRequestDTO("invalidemail", "test", "testname", "testname2");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(userDTO);


        doNothing().when(registrationService).registerUser(userDTO);
        mockMvc.perform(post("/roompro/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please enter a valid email address."));
    }

    @Test
    void shouldNotRegisterUserIfEmailAlreadyExist() throws Exception {

        UserRegistrationRequestDTO userDTO =
                new UserRegistrationRequestDTO("already@gmail.com", "test", "testname", "testname2");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(userDTO);


        doThrow(new Exception("Invalid registration details: User already exists with this email."))
                .when(registrationService).registerUser(userDTO);
        mockMvc.perform(post("/roompro/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid registration details: User already exists with this email."));
    }


    @Test
    void shouldRegisterUser() throws Exception{
        UserRegistrationRequestDTO userDTO =
                new UserRegistrationRequestDTO("test@gmail.com", "test", "testname", "testname2");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(userDTO);


        doNothing().when(registrationService).registerUser(userDTO);
        mockMvc.perform(post("/roompro/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));

    }

    @Test
    void shouldNotLoginIfNullFields() throws Exception{
        UserRequestDTO userDTO =
                new UserRequestDTO(null, null);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/roompro/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("All fields are required."));

    }

    @Test
    void shouldNotLoginIfInvalidEmail() throws Exception{
        UserRequestDTO userDTO =
                new UserRequestDTO("invalidemail", "testpass");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/roompro/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please enter a valid email address."));

    }

    @Test
    void shouldNotLoginIfInvalidCredentials() throws Exception{
        UserRequestDTO userDTO =
                new UserRequestDTO("test@gmail.com", "testpass");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(userDTO);


        when(loginService.authenticate(userDTO)).thenReturn(Optional.empty());

        mockMvc.perform(post("/roompro/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password."));

    }

    @Test
    void shouldLoginIfValidCredentials() throws Exception {
        UserRequestDTO userDTO =
                new UserRequestDTO("test@gmail.com", "testpass");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(userDTO);

        String token = "valid-jwt-token";
        when(loginService.authenticate(userDTO)).thenReturn(Optional.of(token));

        mockMvc.perform(post("/roompro/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }


    }