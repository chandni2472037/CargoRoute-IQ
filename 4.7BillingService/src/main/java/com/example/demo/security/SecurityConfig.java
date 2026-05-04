package com.example.demo.security;
 
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.http.HttpMethod;
 
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
 
                // ── Auth & Internal ───────────────────────────────

                .requestMatchers("/auth/**").permitAll()

                .requestMatchers("/internal/**").permitAll()

                .requestMatchers("/cargoRoute/booking/getBookingById/**").permitAll()
 
                // ── Admin only ────────────────────────────────────

                .requestMatchers("/users/**").hasRole("ADMIN")

                .requestMatchers("/auditlogs/**").hasRole("ADMIN")
 
                // ── BillingLine endpoints ─────────────────────────

                .requestMatchers(HttpMethod.POST,   "/cargoRoute/billing-lines/create").hasAnyRole("BILLINGCLERK","ADMIN")

                .requestMatchers(HttpMethod.GET,    "/cargoRoute/billing-lines/getBy/*").hasAnyRole("BILLINGCLERK","ADMIN","ANALYST")

                .requestMatchers(HttpMethod.GET,    "/cargoRoute/billing-lines/getAll").hasAnyRole("BILLINGCLERK","ADMIN","ANALYST")

                .requestMatchers(HttpMethod.PUT,    "/cargoRoute/billing-lines/update/*").hasAnyRole("BILLINGCLERK","ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/cargoRoute/billing-lines/delete/*").hasRole("ADMIN")
 
                // ── Invoice endpoints ─────────────────────────────

                .requestMatchers(HttpMethod.POST,   "/cargoRoute/invoices/create").hasAnyRole("BILLINGCLERK","ADMIN")

                .requestMatchers(HttpMethod.GET,    "/cargoRoute/invoices/getAll").hasAnyRole("BILLINGCLERK","ADMIN","SHIPPER","ANALYST")

                .requestMatchers(HttpMethod.GET,    "/cargoRoute/invoices/getBy/*").hasAnyRole("BILLINGCLERK","ADMIN","SHIPPER","ANALYST")

                .requestMatchers(HttpMethod.PUT,    "/cargoRoute/invoices/update/*").hasAnyRole("BILLINGCLERK","ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/cargoRoute/invoices/delete/*").hasRole("ADMIN")
 
                // ── Tariff endpoints ──────────────────────────────

                .requestMatchers(HttpMethod.POST,   "/cargoRoute/tariffs/create").hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET,    "/cargoRoute/tariffs/getAll").hasAnyRole("ADMIN","BILLINGCLERK","ANALYST")

                .requestMatchers(HttpMethod.GET,    "/cargoRoute/tariffs/getBy/*").hasAnyRole("ADMIN","BILLINGCLERK","ANALYST")

                .requestMatchers(HttpMethod.PUT,    "/cargoRoute/tariffs/update/*").hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/cargoRoute/tariffs/delete/*").hasRole("ADMIN")
 
                // ── Everything else ───────────────────────────────

                .anyRequest().authenticated()

            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
 
        return http.build();

    }

}

 