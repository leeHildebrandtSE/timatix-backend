package com.timatix.servicebooking.controller;

import com.timatix.servicebooking.service.BusinessMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BusinessMetricsController {

    private final BusinessMetricsService businessMetricsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC')")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        Map<String, Object> metrics = businessMetricsService.getDashboardMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/mechanic/{mechanicId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MECHANIC') and #mechanicId == authentication.principal.id)")
    public ResponseEntity<Map<String, Object>> getMechanicMetrics(@PathVariable Long mechanicId) {
        Map<String, Object> metrics = businessMetricsService.getMechanicMetrics(mechanicId);
        return ResponseEntity.ok(metrics);
    }
}