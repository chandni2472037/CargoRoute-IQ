package com.example.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.DTO.AuthRequestDTO;
import com.example.demo.DTO.AuthResponseDTO;
import com.example.demo.entities.User;
import com.example.demo.services.AuthService;

/**
 * AuthController
 * ---------------
 * Handles authentication APIs.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * User registration API
     */
    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody AuthRequestDTO request) {
        return new ResponseEntity<>(authService.signup(request), HttpStatus.CREATED);
    }

    /**
     * User login API
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}