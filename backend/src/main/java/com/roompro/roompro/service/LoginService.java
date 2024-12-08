package com.roompro.roompro.service;


import com.roompro.roompro.dto.UserLoginDto;
import com.roompro.roompro.model.User;
import com.roompro.roompro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    public boolean loginUser(UserLoginDto userLoginDto){

        // Check if email exists
        User user = userRepository.findByEmail(userLoginDto.getEmail()).orElse(null);

        if(user == null){
            return false;
        }
        else{
            return userLoginDto.getPassword().equals(user.getPassword());
        }
    }

}
