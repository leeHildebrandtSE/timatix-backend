package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.Invoice;
import com.timatix.servicebooking.model.Payment;
import com.timatix.servicebooking.repository.InvoiceRepository;
import com.timatix.servicebooking.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationService notificationService;

    @Value("${payment.gateway.url:https://api.payfast.co.za}")
    private String paymentGatewayUrl;

    @Value("${payment.gateway.merchant-id:}")
    private String merchantId;

    @Value("${payment.gateway.merchant-key:}")
    private String merchantKey;

    public Payment processPayment(Long invoiceId, BigDecimal amount, String paymentMethod) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getPaymentStatus() == Invoice.PaymentStatus.PAID) {
            throw new IllegalArgumentException("Invoice is already paid");
        }

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId(generateTransactionId());
        payment.setStatus(Payment.PaymentStatus.PENDING);

        try {
            // Process payment with gateway
            PaymentResult result = processWithGateway(payment);

            if (result.isSuccess()) {
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
                payment.setGatewayReference(result.getGatewayReference());

                // Update invoice status
                updateInvoicePaymentStatus(invoice, amount);

                // Send notification
                notificationService.notifyPaymentReceived(invoice, payment);

                log.info("Payment processed successfully for invoice: {}", invoiceId);
            } else {
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason(result.getErrorMessage());
                log.warn("Payment failed for invoice: {} - {}", invoiceId, result.getErrorMessage());
            }
        } catch (Exception e) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            log.error("Payment processing error for invoice: {}", invoiceId, e);
        }

        return paymentRepository.save(payment);
    }

    public Payment refundPayment(Long paymentId, BigDecimal amount, String reason) {
        Payment originalPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (originalPayment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new IllegalArgumentException("Can only refund completed payments");
        }

        Payment refund = new Payment();
        refund.setInvoice(originalPayment.getInvoice());
        refund.setAmount(amount.negate()); // Negative amount for refund
        refund.setPaymentMethod(originalPayment.getPaymentMethod());
        refund.setTransactionId(generateTransactionId());
        refund.setStatus(Payment.PaymentStatus.PENDING);
        refund.setRefundReason(reason);
        refund.setOriginalPayment(originalPayment);

        try {
            PaymentResult result = processRefundWithGateway(refund);

            if (result.isSuccess()) {
                refund.setStatus(Payment.PaymentStatus.COMPLETED);
                refund.setGatewayReference(result.getGatewayReference());

                // Update invoice if fully refunded
                updateInvoiceAfterRefund(originalPayment.getInvoice(), amount);

                log.info("Refund processed successfully for payment: {}", paymentId);
            } else {
                refund.setStatus(Payment.PaymentStatus.FAILED);
                refund.setFailureReason(result.getErrorMessage());
                log.warn("Refund failed for payment: {} - {}", paymentId, result.getErrorMessage());
            }
        } catch (Exception e) {
            refund.setStatus(Payment.PaymentStatus.FAILED);
            refund.setFailureReason(e.getMessage());
            log.error("Refund processing error for payment: {}", paymentId, e);
        }

        return paymentRepository.save(refund);
    }

    public List<Payment> getPaymentsByInvoice(Long invoiceId) {
        return paymentRepository.findByInvoiceIdOrderByCreatedAtDesc(invoiceId);
    }

    public List<Payment> getPendingPayments() {
        return paymentRepository.findByStatus(Payment.PaymentStatus.PENDING);
    }

    public String generatePaymentUrl(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // Generate secure payment URL for external gateway
        String paymentToken = generatePaymentToken(invoice);

        return String.format("%s/payment?token=%s&merchant_id=%s&amount=%.2f&item_name=%s",
                paymentGatewayUrl, paymentToken, merchantId,
                invoice.getTotalAmount(), "Service Invoice " + invoice.getInvoiceNumber());
    }

    public boolean canAccessInvoicePayments(Long invoiceId, String userEmail) {
        try {
            Invoice invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));

            String clientEmail = invoice.getServiceRequest().getClient().getEmail();
            return clientEmail.equals(userEmail);
        } catch (Exception e) {
            log.error("Error checking invoice access for user: {}", userEmail, e);
            return false;
        }
    }

    private PaymentResult processWithGateway(Payment payment) {
        // Mock payment gateway integration
        // In production, integrate with actual payment gateway like PayFast, Stripe, etc.
        log.info("Processing payment with gateway: {}", payment.getTransactionId());

        // Simulate payment processing
        if (payment.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            return new PaymentResult(true, "TXN_" + UUID.randomUUID().toString(), null);
        } else {
            return new PaymentResult(false, null, "Invalid amount");
        }
    }

    private PaymentResult processRefundWithGateway(Payment refund) {
        // Mock refund processing
        log.info("Processing refund with gateway: {}", refund.getTransactionId());

        return new PaymentResult(true, "REF_" + UUID.randomUUID().toString(), null);
    }

    private void updateInvoicePaymentStatus(Invoice invoice, BigDecimal paidAmount) {
        BigDecimal totalPaid = paymentRepository.getTotalPaidAmount(invoice.getId());
        totalPaid = totalPaid.add(paidAmount);

        if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setPaymentStatus(Invoice.PaymentStatus.PAID);
            invoice.setPaidDate(LocalDateTime.now());
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setPaymentStatus(Invoice.PaymentStatus.PARTIAL);
        }

        invoiceRepository.save(invoice);
    }

    private void updateInvoiceAfterRefund(Invoice invoice, BigDecimal refundAmount) {
        BigDecimal totalPaid = paymentRepository.getTotalPaidAmount(invoice.getId());

        if (totalPaid.compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setPaymentStatus(Invoice.PaymentStatus.UNPAID);
            invoice.setPaidDate(null);
        } else if (totalPaid.compareTo(invoice.getTotalAmount()) < 0) {
            invoice.setPaymentStatus(Invoice.PaymentStatus.PARTIAL);
        }

        invoiceRepository.save(invoice);
    }

    private String generateTransactionId() {
        return "TIM_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String generatePaymentToken(Invoice invoice) {
        return UUID.randomUUID().toString();
    }

    // Inner classes for payment processing
    private static class PaymentResult {
        private final boolean success;
        private final String gatewayReference;
        private final String errorMessage;

        public PaymentResult(boolean success, String gatewayReference, String errorMessage) {
            this.success = success;
            this.gatewayReference = gatewayReference;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() { return success; }
        public String getGatewayReference() { return gatewayReference; }
        public String getErrorMessage() { return errorMessage; }
    }
}