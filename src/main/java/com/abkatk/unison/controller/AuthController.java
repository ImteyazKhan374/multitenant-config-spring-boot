package com.abkatk.unison.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abkatk.unison.dto.AuthRequest;
import com.abkatk.unison.util.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    // this need to hit the database to fetch the user details to generate the token
//    @Autowired
//    private CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
    			// Authenticate the user
		// This will throw an exception if the credentials are invalid
		// Uncomment the following lines if you want to use authenticationManager
//        authenticationManager.authenticate(
//            new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
//        );

       // final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(authRequest);
        return ResponseEntity.ok(jwt);
    }
}

