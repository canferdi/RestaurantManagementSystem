package com.ferdican.restaurantsystem.services;

import com.ferdican.restaurantsystem.entity.Users;
import com.ferdican.restaurantsystem.repository.UsersRepository;
import com.ferdican.restaurantsystem.util.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailservice implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Autowired
    public CustomUserDetailservice(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Could " +
                "not found user"));
        return new CustomUserDetails(user);
    }
}
