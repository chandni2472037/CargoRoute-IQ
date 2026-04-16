package com.example.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DTO.InternalUserDTO;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repositories.UserRepository;

@RestController
@RequestMapping("/cargoRoute/internal")
public class InternalUserController {

    private final UserRepository repo;

    public InternalUserController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/users/{id}/exists")
    public Boolean userExists(@PathVariable Long id) {
        return repo.existsById(id);
    }
    

    @GetMapping("/users/{id}")
    public InternalUserDTO getUserInternal(@PathVariable Long id) {

        return repo.findById(id)
            .map(user -> new InternalUserDTO(
                user.getUserID(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
            ))
            .orElseThrow(() ->
                new ResourceNotFoundException("User not found with id: " + id)
            );
    }


}