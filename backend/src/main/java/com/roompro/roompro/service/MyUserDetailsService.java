package com.roompro.roompro.service;


import com.roompro.roompro.model.UserPrincipal;
import com.roompro.roompro.model.Users;
import com.roompro.roompro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Users user = repo.findByEmail(email);

        if(user==null){
            throw  new UsernameNotFoundException("User Not Found");
        }


        return new UserPrincipal(user);
    }


}

