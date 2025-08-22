package com.timatix.servicebooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials for authentication
        config.setAllowCredentials(true);

        // Allow specific origins for development
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",           // Local development
                "http://192.168.*.*:*",         // Local network
                "http://10.0.2.2:*",           // Android emulator
                "exp://localhost:*",           // Expo development
                "exp://192.168.*.*:*",         // Expo on network
                "capacitor://localhost",       // Capacitor mobile
                "ionic://localhost",           // Ionic mobile
                "http://localhost",            // Additional localhost
                "https://localhost:*"          // HTTPS localhost
        ));

        // Allow all headers
        config.setAllowedHeaders(Arrays.asList("*"));

        // Allow all HTTP methods
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        // Expose headers that might be needed by the frontend
        config.setExposedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With", "Accept",
                "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"
        ));

        // Set max age for preflight requests (1 hour)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed origins for production and development
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "http://192.168.*.*:*",
                "http://10.0.2.2:*",
                "exp://localhost:*",
                "exp://192.168.*.*:*",
                "capacitor://localhost",
                "ionic://localhost"
        ));

        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}