package com.timatix.servicebooking.repository;

import com.timatix.servicebooking.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByPaymentStatus(Invoice.PaymentStatus status);

    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :currentTime AND i.paymentStatus = 'UNPAID'")
    List<Invoice> findOverdueInvoices(@Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT i FROM Invoice i WHERE i.serviceRequest.client.id = :clientId")
    List<Invoice> findByClientId(@Param("clientId") Long clientId);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.paymentStatus = 'PAID' AND i.paidDate BETWEEN :startDate AND :endDate")
    Double getTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}