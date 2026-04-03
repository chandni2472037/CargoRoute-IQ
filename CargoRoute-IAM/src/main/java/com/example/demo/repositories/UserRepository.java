package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entities.User;

/**
 * UserRepository
 * Handles all database operations related to User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email address.
     * Used mainly during authentication/login.
     *
     * @param email user's email
     * @return User entity
     */
    User findByEmail(String email);
}