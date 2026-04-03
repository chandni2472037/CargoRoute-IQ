package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * JWT filter to validate token and set SecurityContext
     */
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // Stateless JWT → CSRF disabled
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // Authentication endpoints
                .requestMatchers("/auth/**").permitAll()

                // User management → ADMIN only
                .requestMatchers("/users/**").hasRole("Admin")

                // Audit logs → ADMIN only
                .requestMatchers("/auditlogs/**").hasRole("Admin")

                // Everything else must be authenticated
                .anyRequest().authenticated()
            )

            // JWT validation before Spring Security
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}