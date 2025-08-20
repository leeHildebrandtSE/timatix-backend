package com.timatix.servicebooking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HealthController {

    private final DataSource dataSource;

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();

        try {
            // Check database connection
            try (Connection connection = dataSource.getConnection()) {
                boolean isValid = connection.isValid(5);
                health.put("database", isValid ? "UP" : "DOWN");
            }

            health.put("status", "UP");
            health.put("timestamp", LocalDateTime.now());
            health.put("service", "timatix-booking-services");

            return ResponseEntity.ok(health);

        } catch (Exception e) {
            log.error("Health check failed", e);
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            health.put("timestamp", LocalDateTime.now());

            return ResponseEntity.status(503).body(health);
        }
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();

        info.put("application", "Timatix Booking Services");
        info.put("description", "Vehicle service management and booking system");
        info.put("version", "1.0.0");
        info.put("timestamp", LocalDateTime.now());
        info.put("timezone", "Africa/Johannesburg");

        // Add JVM info
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> jvm = new HashMap<>();
        jvm.put("totalMemory", runtime.totalMemory());
        jvm.put("freeMemory", runtime.freeMemory());
        jvm.put("maxMemory", runtime.maxMemory());
        jvm.put("processors", runtime.availableProcessors());
        info.put("jvm", jvm);

        return ResponseEntity.ok(info);
    }

    @GetMapping("/ready")
    public ResponseEntity<Map<String, String>> ready() {
        try {
            // Check if application is ready to serve requests
            try (Connection connection = dataSource.getConnection()) {
                connection.isValid(2);
            }

            Map<String, String> response = new HashMap<>();
            response.put("status", "READY");
            response.put("message", "Application is ready to serve requests");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Readiness check failed", e);
            Map<String, String> response = new HashMap<>();
            response.put("status", "NOT_READY");
            response.put("message", "Application is not ready: " + e.getMessage());

            return ResponseEntity.status(503).body(response);
        }
    }

    @GetMapping("/live")
    public ResponseEntity<Map<String, String>> live() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ALIVE");
        response.put("message", "Application is running");
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }
}