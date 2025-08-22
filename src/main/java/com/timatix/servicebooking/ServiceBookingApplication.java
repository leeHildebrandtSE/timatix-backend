package com.timatix.servicebooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class ServiceBookingApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ServiceBookingApplication.class);

        // Ensure server binds to all interfaces
        System.setProperty("server.address", "0.0.0.0");
        System.setProperty("server.port", "8081");

        app.run(args);

        System.out.println("🚀 Spring Boot server running on http://0.0.0.0:8081");
        System.out.println("📱 Mobile devices can access: http://192.168.18.7:8081");
        System.out.println("💻 Local access: http://localhost:8081");
        System.out.println("🔍 Health check: http://localhost:8081/api/health");
    }

    // Health check endpoint for testing connectivity
    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Spring Boot backend server is running");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("port", 8081);
        response.put("profiles", System.getProperty("spring.profiles.active", "default"));
        return response;
    }
}