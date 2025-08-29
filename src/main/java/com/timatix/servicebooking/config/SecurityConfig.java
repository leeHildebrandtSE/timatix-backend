package com.timatix.servicebooking.config;

import com.timatix.servicebooking.security.JwtAuthenticationEntryPoint;
import com.timatix.servicebooking.security.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults()) // Use existing CORS configuration
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        // Auth endpoints - support both patterns
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/users/register", "/users/login").permitAll()

                        // Health endpoints - support multiple patterns
                        .requestMatchers("/health/**").permitAll()
                        .requestMatchers("/api/health/**").permitAll()
                        .requestMatchers("/api/actuator/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // Public endpoints
                        .requestMatchers("/services/**").permitAll()
                        .requestMatchers("/api/services/**").permitAll()
                        .requestMatchers("/booking-slots/available/**").permitAll()
                        .requestMatchers("/api/booking-slots/available/**").permitAll()

                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // CORS configuration is handled by existing CorsConfig class
}