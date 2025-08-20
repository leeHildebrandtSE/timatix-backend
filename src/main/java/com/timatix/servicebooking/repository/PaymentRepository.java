package com.timatix.servicebooking.repository;

import com.timatix.servicebooking.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByInvoiceIdOrderByCreatedAtDesc(Long invoiceId);

    List<Payment> findByStatus(Payment.PaymentStatus status);

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByGatewayReference(String gatewayReference);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.invoice.id = :invoiceId AND p.status = 'COMPLETED'")
    BigDecimal getTotalPaidAmount(@Param("invoiceId") Long invoiceId);

    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < :cutoffTime")
    List<Payment> findStalePayments(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT p FROM Payment p WHERE p.invoice.serviceRequest.client.id = :clientId ORDER BY p.createdAt DESC")
    List<Payment> findByClientIdOrderByCreatedAtDesc(@Param("clientId") Long clientId);

    @Query("SELECT p FROM Payment p WHERE p.paymentMethod = :method AND p.status = 'COMPLETED'")
    List<Payment> findSuccessfulPaymentsByMethod(@Param("method") String method);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.processedAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'COMPLETED' AND DATE(p.processedAt) = CURRENT_DATE")
    Long countTodaysSuccessfulPayments();

    @Query("SELECT p FROM Payment p WHERE p.originalPayment.id = :originalPaymentId")
    List<Payment> findRefundsByOriginalPayment(@Param("originalPaymentId") Long originalPaymentId);
}