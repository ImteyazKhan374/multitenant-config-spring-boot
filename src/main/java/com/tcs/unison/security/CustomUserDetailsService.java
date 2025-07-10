package com.tcs.unison.security;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Static user for demo. Replace with DB lookup in real apps.
        if ("admin".equals(username)) {
            return new User("admin", new BCryptPasswordEncoder().encode("admin123"), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
