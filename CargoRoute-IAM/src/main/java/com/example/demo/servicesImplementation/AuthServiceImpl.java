package com.example.demo.servicesImplementation;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.AuthRequestDTO;
import com.example.demo.DTO.AuthResponseDTO;
import com.example.demo.entities.User;
import com.example.demo.enums.UserRole;
import com.example.demo.exceptions.DuplicateEmailException;
import com.example.demo.exceptions.InvalidCredentialsException;
import com.example.demo.exceptions.InvalidUserRoleException;
import com.example.demo.exceptions.UserAccountDisabledException;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.services.AuthService;

/**
 * AuthServiceImpl
 * ----------------
 * Contains signup and login logic.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository repo,
                           PasswordEncoder encoder,
                           JwtUtil jwtUtil) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    // SIGNUP
    @Override
    public User signup(AuthRequestDTO request) {

        // Email uniqueness
        if (repo.findByEmail(request.getEmail()) != null) {
            throw new DuplicateEmailException(
                "Email already exists: " + request.getEmail()
            );
        }

        if (request.getRole() == null) {
            throw new InvalidUserRoleException("User role must not be null");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        // SAFE ROLE CONVERSION (THIS FIXES YOUR ISSUE)
        user.setRole(UserRole.from(request.getRole()));

        user.setPassword(encoder.encode(request.getPassword()));
        user.setStatus(
            request.getStatus() != null ? request.getStatus() : "ACTIVE"
        );

        return repo.save(user);
    }

    // LOGIN
    @Override
    public AuthResponseDTO login(AuthRequestDTO request) {

        User user = repo.findByEmail(request.getEmail());

        if (user == null ||
            !encoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new UserAccountDisabledException(
                "User account is not active"
            );
        }

        String token = jwtUtil.generateToken(
            user.getEmail(),
            user.getRole().name()
        );

        return new AuthResponseDTO(token);
    }
}
