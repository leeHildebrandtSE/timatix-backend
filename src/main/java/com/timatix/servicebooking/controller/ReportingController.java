package com.timatix.servicebooking.controller;

import com.timatix.servicebooking.service.ReportingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportingController {

    private final ReportingService reportingService;

    @GetMapping("/financial")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getFinancialReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, Object> report = reportingService.generateFinancialReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/operational")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getOperationalReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, Object> report = reportingService.generateOperationalReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/customer/{clientId}")
    @PreAuthorize("hasRole('ADMIN') or #clientId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getCustomerReport(@PathVariable Long clientId) {
        Map<String, Object> report = reportingService.generateCustomerReport(clientId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/mechanic/{mechanicId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MECHANIC') and #mechanicId == authentication.principal.id)")
    public ResponseEntity<Map<String, Object>> getMechanicReport(@PathVariable Long mechanicId) {
        Map<String, Object> report = reportingService.generateMechanicReport(mechanicId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC')")
    public ResponseEntity<List<Map<String, Object>>> getInventoryReport() {
        List<Map<String, Object>> report = reportingService.generateInventoryReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC')")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        Map<String, Object> summary = reportingService.generateDashboardSummary();
        return ResponseEntity.ok(summary);
    }
}