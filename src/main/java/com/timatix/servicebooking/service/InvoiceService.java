package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.Invoice;
import com.timatix.servicebooking.model.ServiceRequest;
import com.timatix.servicebooking.repository.InvoiceRepository;
import com.timatix.servicebooking.repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final NotificationService notificationService;

    public Invoice createInvoiceFromQuote(Long serviceRequestId) {
        ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        if (serviceRequest.getQuote() == null) {
            throw new IllegalArgumentException("No approved quote found for this service request");
        }

        Invoice invoice = new Invoice();
        invoice.setServiceRequest(serviceRequest);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setSubtotal(serviceRequest.getQuote().getTotalAmount());

        // Calculate tax (15% VAT for South Africa)
        BigDecimal taxRate = new BigDecimal("0.15");
        BigDecimal taxAmount = invoice.getSubtotal().multiply(taxRate);
        invoice.setTaxAmount(taxAmount);

        invoice.setTotalAmount(invoice.getSubtotal().add(taxAmount));
        invoice.setLineItemsJson(serviceRequest.getQuote().getLineItemsJson());

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice created: {}", savedInvoice.getInvoiceNumber());

        return savedInvoice;
    }

    public Invoice markAsPaid(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setPaymentStatus(Invoice.PaymentStatus.PAID);
        invoice.setPaidDate(LocalDateTime.now());

        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getOverdueInvoices() {
        return invoiceRepository.findOverdueInvoices(LocalDateTime.now());
    }

    private String generateInvoiceNumber() {
        String prefix = "TIM";
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        long count = invoiceRepository.count() + 1;
        return String.format("%s-%s-%04d", prefix, datePart, count);
    }
}