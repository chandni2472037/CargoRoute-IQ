package com.example.demo.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) //Enables method-level security
public class SecurityConfig {
     
	
    //reads and validates JWT token from incoming requests
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
                    .requestMatchers("/auth/**").permitAll() //	 Shipper,Dispatcher,FleetManager,Driver,WarehouseManager,BillingClerk,Admin,Analyst
                    .requestMatchers("/shippers/**").hasRole("Shipper")
                    .requestMatchers("/handovers/**").hasRole("WarehouseManager")
                    .requestMatchers("/bookings/**").hasRole("Shipper")
                    .requestMatchers("/vehicles/**").hasRole("Admin") 
                    .requestMatchers("/vehicleAvailability/**").hasRole("FleetManager")
                    .requestMatchers("/loads/**").hasRole("Dispatcher") //aasign loads-dispatcher
                    .requestMatchers("/routes/**").hasRole("Dispatcher")
                    .requestMatchers("/routingRules/**").hasRole("Admin")
                    .requestMatchers("/drivers/**").hasRole("Dispatcher")
                    .requestMatchers("/dispatch/**").hasRole("Dispatcher")
                    .requestMatchers("/driverack/**").hasRole("Driver")
                    .requestMatchers("/invoices/**").hasRole("BillingClerk") //creation- BillingClerk SEE- Shipper, Admin
                    .requestMatchers("/kpis/**").hasRole("Analyst")  //analyst see- fleet manager, admin,analyst
                    .requestMatchers("/claims/**").hasRole("Shipper")
                    .requestMatchers("/exceptions/**").hasRole("Dispatcher")
                    .requestMatchers("/tasks/**").hasRole("Admin")
                    .requestMatchers("/manifests/**").hasRole("WarehouseManager")
                    .requestMatchers("/notifications/**").permitAll()
                    .requestMatchers("/pod/**").hasRole("Driver")
                    .requestMatchers("/reports/**").hasRole("Analyst")
                    .requestMatchers("/tariffs/**").hasRole("BillingClerk")  //creation- BillingClerk SEE- Shipper, Admin
                    .requestMatchers("/billing-lines/**").hasRole("BillingClerk") 
                    
                    //admin access
                    .requestMatchers("/users/**").hasRole("Admin")
                    .requestMatchers("/auditlogs/**").hasRole("Admin")
                    .anyRequest().authenticated()
            )
            
            
            //allows the JWT to be validated
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
 
        return http.build(); //builds the security
}

}






