package com.example.demo.services;

import java.util.List;
import com.example.demo.DTO.UserDTO;

/**
 * UserService
 * ------------
 * Defines business logic operations for User.
 */
public interface UserService {

    // Creates or updates a user
    UserDTO saveUser(UserDTO userDTO);

    // Fetches all users from the system
    List<UserDTO> getAllUsers();

    // Fetches a single user using ID
    UserDTO getUserById(Long id);
}