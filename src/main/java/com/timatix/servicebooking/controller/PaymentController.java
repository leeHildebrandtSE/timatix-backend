package com.timatix.servicebooking.controller;

import com.timatix.servicebooking.model.Payment;
import com.timatix.servicebooking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> paymentRequest) {
        try {
            Long invoiceId = Long.valueOf(paymentRequest.get("invoiceId").toString());
            BigDecimal amount = new BigDecimal(paymentRequest.get("amount").toString());
            String paymentMethod = (String) paymentRequest.get("paymentMethod");

            Payment payment = paymentService.processPayment(invoiceId, amount, paymentMethod);

            Map<String, Object> response = new HashMap<>();
            response.put("payment", payment);
            response.put("success", payment.isSuccessful());
            response.put("message", payment.isSuccessful() ? "Payment processed successfully" : "Payment failed");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing payment", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/refund/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> refundPayment(
            @PathVariable Long paymentId,
            @RequestBody Map<String, Object> refundRequest) {
        try {
            BigDecimal amount = new BigDecimal(refundRequest.get("amount").toString());
            String reason = (String) refundRequest.get("reason");

            Payment refund = paymentService.refundPayment(paymentId, amount, reason);

            Map<String, Object> response = new HashMap<>();
            response.put("refund", refund);
            response.put("success", refund.isSuccessful());
            response.put("message", refund.isSuccessful() ? "Refund processed successfully" : "Refund failed");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing refund", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasRole('ADMIN') or @paymentService.canAccessInvoicePayments(#invoiceId, authentication.principal.username)")
    public ResponseEntity<List<Payment>> getPaymentsByInvoice(@PathVariable Long invoiceId) {
        List<Payment> payments = paymentService.getPaymentsByInvoice(invoiceId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getPendingPayments() {
        List<Payment> payments = paymentService.getPendingPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/payment-url/{invoiceId}")
    @PreAuthorize("hasRole('ADMIN') or @paymentService.canAccessInvoicePayments(#invoiceId, authentication.principal.username)")
    public ResponseEntity<?> generatePaymentUrl(@PathVariable Long invoiceId) {
        try {
            String paymentUrl = paymentService.generatePaymentUrl(invoiceId);

            Map<String, String> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            response.put("message", "Payment URL generated successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating payment URL", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/webhook/payment-notification")
    public ResponseEntity<?> handlePaymentWebhook(@RequestBody Map<String, Object> webhookData) {
        try {
            // Handle payment gateway webhook notifications
            log.info("Received payment webhook: {}", webhookData);

            String transactionId = (String) webhookData.get("transaction_id");
            String status = (String) webhookData.get("status");

            // Process webhook notification
            // In production, verify webhook signature and update payment status

            Map<String, String> response = new HashMap<>();
            response.put("status", "received");
            response.put("message", "Webhook processed successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing payment webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}