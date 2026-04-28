package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.*;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // Auth endpoints
                .requestMatchers("/auth/**").permitAll()

                // Internal calls
                .requestMatchers("/internal/**").permitAll()

                // Allow service-to-service booking lookup without auth (used by ExceptionService)
                .requestMatchers("/cargoRoute/booking/getBookingById/**").permitAll()

                // Admin only
                .requestMatchers("/users/**").hasRole("ADMIN")
                .requestMatchers("/auditlogs/**").hasRole("ADMIN")

                // ── Booking endpoints ────────────────────────────────────────────────
                // addBooking — SHIPPER, ADMIN only
                .requestMatchers(HttpMethod.POST,  "/cargoRoute/booking/addBooking").hasAnyRole("SHIPPER", "ADMIN")
                // importBookings — SHIPPER, ADMIN only
                .requestMatchers(HttpMethod.POST,  "/cargoRoute/booking/importBookings").hasAnyRole("SHIPPER", "ADMIN")
                // getBookings — SHIPPER, DISPATCHER, ADMIN, FLEETMANAGER, WAREHOUSEMANAGER, ANALYST (read-only)
                .requestMatchers(HttpMethod.GET,   "/cargoRoute/booking/getBookings").hasAnyRole("SHIPPER", "DISPATCHER", "ADMIN", "FLEETMANAGER", "WAREHOUSEMANAGER", "BILLINGCLERK", "ANALYST")
                // getBookingById — permitAll (service-to-service; already declared above, repeated here for readability)
                // updateBookingStatus — DISPATCHER, DRIVER (operational only)
                .requestMatchers(HttpMethod.PATCH, "/cargoRoute/booking/updateBookingStatus/*").hasAnyRole("DISPATCHER", "DRIVER")
                // getBookingsByStatus — all authenticated roles
                .requestMatchers(HttpMethod.GET,   "/cargoRoute/booking/getBookingsByStatus/*").hasAnyRole("SHIPPER", "DISPATCHER", "DRIVER", "WAREHOUSEMANAGER",  "BILLINGCLERK", "ANALYST", "ADMIN")
                // getBookingsByShipperID — SHIPPER, DISPATCHER, ADMIN
                .requestMatchers(HttpMethod.GET,   "/cargoRoute/booking/getBookingsByShipperID/*").hasAnyRole("SHIPPER", "DISPATCHER", "ADMIN", "BILLINGCLERK", "ANALYST")

                // ── Shipper endpoints ────────────────────────────────────────────────
                // addShipper  — ADMIN only
                .requestMatchers(HttpMethod.POST, "/cargoRoute/shipper/addShipper").hasRole("ADMIN")
                // updateShipper — ADMIN only
                .requestMatchers(HttpMethod.PUT,  "/cargoRoute/shipper/updateShipper/*").hasRole("ADMIN")
                // getShippers — ADMIN, DISPATCHER, SHIPPER, ANALYST, FLEETMANAGER
                .requestMatchers(HttpMethod.GET,  "/cargoRoute/shipper/getShippers").hasAnyRole("ADMIN", "DISPATCHER", "SHIPPER", "ANALYST", "FLEETMANAGER", "WAREHOUSEMANAGER",  "BILLINGCLERK")
                // getShipper/{id} — ADMIN, DISPATCHER, SHIPPER, FLEETMANAGER
                .requestMatchers(HttpMethod.GET,  "/cargoRoute/shipper/getShipper/*").hasAnyRole("ADMIN", "DISPATCHER", "SHIPPER", "FLEETMANAGER", "WAREHOUSEMANAGER", "BILLINGCLERK", "ANALYST")
                // getShippersByStatus/{status} — ADMIN, DISPATCHER, ANALYST
                .requestMatchers(HttpMethod.GET,  "/cargoRoute/shipper/getShippersByStatus/*").hasAnyRole("ADMIN", "DISPATCHER", "ANALYST", "WAREHOUSEMANAGER",  "BILLINGCLERK")

                // Everything else
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}