package com.system.blog.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.system.blog.application.model.RegistrationRequest;
import com.system.blog.application.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class RegistrationController {

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        if (userService.findUserByUsername(registrationRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        // Encode password
        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());

        userService.registerUser(registrationRequest.getUsername(), encodedPassword);

        return ResponseEntity.ok("User registered successfully");
    }
}
