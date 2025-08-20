package com.timatix.servicebooking.controller;

import com.timatix.servicebooking.model.Invoice;
import com.timatix.servicebooking.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/generate/{serviceRequestId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC')")
    public ResponseEntity<?> generateInvoice(@PathVariable Long serviceRequestId) {
        try {
            Invoice invoice = invoiceService.createInvoiceFromQuote(serviceRequestId);
            return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
        } catch (Exception e) {
            log.error("Error generating invoice", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{invoiceId}/mark-paid")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> markInvoiceAsPaid(@PathVariable Long invoiceId) {
        try {
            Invoice invoice = invoiceService.markAsPaid(invoiceId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invoice marked as paid");
            response.put("invoice", invoice);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error marking invoice as paid", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Invoice>> getOverdueInvoices() {
        List<Invoice> invoices = invoiceService.getOverdueInvoices();
        return ResponseEntity.ok(invoices);
    }
}