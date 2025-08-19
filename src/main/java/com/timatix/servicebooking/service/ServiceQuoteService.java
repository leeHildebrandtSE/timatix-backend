package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.ServiceQuote;
import com.timatix.servicebooking.model.ServiceRequest;
import com.timatix.servicebooking.model.User;
import com.timatix.servicebooking.repository.ServiceQuoteRepository;
import com.timatix.servicebooking.repository.ServiceRequestRepository;
import com.timatix.servicebooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ServiceQuoteService {

    private final ServiceQuoteRepository serviceQuoteRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;

    public List<ServiceQuote> getAllQuotes() {
        return serviceQuoteRepository.findAll();
    }

    public Optional<ServiceQuote> getQuoteById(Long id) {
        return serviceQuoteRepository.findById(id);
    }

    public Optional<ServiceQuote> getQuoteByRequestId(Long requestId) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found with id: " + requestId));
        return serviceQuoteRepository.findByRequest(request);
    }

    public List<ServiceQuote> getQuotesByMechanic(Long mechanicId) {
        return serviceQuoteRepository.findByMechanicId(mechanicId);
    }

    public List<ServiceQuote> getQuotesByStatus(ServiceQuote.ApprovalStatus status) {
        return serviceQuoteRepository.findByApprovalStatus(status);
    }

    public List<ServiceQuote> getPendingQuotes() {
        return serviceQuoteRepository.findByApprovalStatus(ServiceQuote.ApprovalStatus.PENDING);
    }

    public ServiceQuote createQuote(ServiceQuote quote) {
        validateQuoteForCreation(quote);

        // Check if quote already exists for this request
        Optional<ServiceQuote> existingQuote = serviceQuoteRepository.findByRequest(quote.getRequest());
        if (existingQuote.isPresent()) {
            throw new IllegalArgumentException("Quote already exists for this service request");
        }

        // Set default values
        if (quote.getApprovalStatus() == null) {
            quote.setApprovalStatus(ServiceQuote.ApprovalStatus.PENDING);
        }

        // Update service request status
        ServiceRequest request = quote.getRequest();
        request.setStatus(ServiceRequest.RequestStatus.QUOTE_SENT);
        serviceRequestRepository.save(request);

        log.info("Creating new service quote for request ID: {}", quote.getRequest().getId());
        return serviceQuoteRepository.save(quote);
    }

    public ServiceQuote createQuoteForRequest(Long requestId, Long mechanicId, ServiceQuote quoteDetails) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found with id: " + requestId));

        User mechanic = userRepository.findById(mechanicId)
                .orElseThrow(() -> new RuntimeException("Mechanic not found with id: " + mechanicId));

        if (mechanic.getRole() != User.Role.MECHANIC && mechanic.getRole() != User.Role.ADMIN) {
            throw new IllegalArgumentException("User is not a mechanic");
        }

        ServiceQuote quote = new ServiceQuote();
        quote.setRequest(request);
        quote.setMechanic(mechanic);
        quote.setLineItemsJson(quoteDetails.getLineItemsJson());
        quote.setLabourCost(quoteDetails.getLabourCost());
        quote.setPartsCost(quoteDetails.getPartsCost());
        quote.setTotalAmount(quoteDetails.getTotalAmount());
        quote.setNotes(quoteDetails.getNotes());
        quote.setValidUntil(quoteDetails.getValidUntil());

        // Auto-calculate total if not provided
        if (quote.getTotalAmount() == null) {
            BigDecimal labourCost = quote.getLabourCost() != null ? quote.getLabourCost() : BigDecimal.ZERO;
            BigDecimal partsCost = quote.getPartsCost() != null ? quote.getPartsCost() : BigDecimal.ZERO;
            quote.setTotalAmount(labourCost.add(partsCost));
        }

        return createQuote(quote);
    }

    public ServiceQuote updateQuote(Long id, ServiceQuote quoteDetails) {
        ServiceQuote quote = serviceQuoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service quote not found with id: " + id));

        // Only allow updates if quote is still pending
        if (quote.getApprovalStatus() != ServiceQuote.ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Cannot update quote that has been approved or declined");
        }

        quote.setLineItemsJson(quoteDetails.getLineItemsJson());
        quote.setLabourCost(quoteDetails.getLabourCost());
        quote.setPartsCost(quoteDetails.getPartsCost());
        quote.setTotalAmount(quoteDetails.getTotalAmount());
        quote.setNotes(quoteDetails.getNotes());
        quote.setValidUntil(quoteDetails.getValidUntil());

        // Auto-calculate total if not provided
        if (quote.getTotalAmount() == null) {
            BigDecimal labourCost = quote.getLabourCost() != null ? quote.getLabourCost() : BigDecimal.ZERO;
            BigDecimal partsCost = quote.getPartsCost() != null ? quote.getPartsCost() : BigDecimal.ZERO;
            quote.setTotalAmount(labourCost.add(partsCost));
        }

        log.info("Updating service quote with id: {}", id);
        return serviceQuoteRepository.save(quote);
    }

    public ServiceQuote approveQuote(Long id) {
        ServiceQuote quote = serviceQuoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service quote not found with id: " + id));

        if (quote.getApprovalStatus() != ServiceQuote.ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Quote is not in pending status");
        }

        // Check if quote is still valid
        if (quote.getValidUntil() != null && quote.getValidUntil().isBefore(LocalDateTime.now())) {
            quote.setApprovalStatus(ServiceQuote.ApprovalStatus.EXPIRED);
            serviceQuoteRepository.save(quote);
            throw new IllegalArgumentException("Quote has expired");
        }

        quote.setApprovalStatus(ServiceQuote.ApprovalStatus.ACCEPTED);
        quote.setApprovedAt(LocalDateTime.now());

        // Update service request status
        ServiceRequest request = quote.getRequest();
        request.setStatus(ServiceRequest.RequestStatus.QUOTE_APPROVED);
        serviceRequestRepository.save(request);

        log.info("Approving service quote with id: {}", id);
        return serviceQuoteRepository.save(quote);
    }

    public ServiceQuote declineQuote(Long id) {
        ServiceQuote quote = serviceQuoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service quote not found with id: " + id));

        if (quote.getApprovalStatus() != ServiceQuote.ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Quote is not in pending status");
        }

        quote.setApprovalStatus(ServiceQuote.ApprovalStatus.DECLINED);

        // Update service request status
        ServiceRequest request = quote.getRequest();
        request.setStatus(ServiceRequest.RequestStatus.QUOTE_DECLINED);
        serviceRequestRepository.save(request);

        log.info("Declining service quote with id: {}", id);
        return serviceQuoteRepository.save(quote);
    }

    public void deleteQuote(Long id) {
        ServiceQuote quote = serviceQuoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service quote not found with id: " + id));

        if (quote.getApprovalStatus() == ServiceQuote.ApprovalStatus.ACCEPTED) {
            throw new IllegalArgumentException("Cannot delete approved quote");
        }

        // Reset service request status if needed
        ServiceRequest request = quote.getRequest();
        if (request.getStatus() == ServiceRequest.RequestStatus.QUOTE_SENT) {
            request.setStatus(ServiceRequest.RequestStatus.PENDING_QUOTE);
            serviceRequestRepository.save(request);
        }

        log.info("Deleting service quote with id: {}", id);
        serviceQuoteRepository.deleteById(id);
    }

    public void markExpiredQuotes() {
        List<ServiceQuote> expiredQuotes = serviceQuoteRepository.findExpiredPendingQuotes(LocalDateTime.now());
        for (ServiceQuote quote : expiredQuotes) {
            quote.setApprovalStatus(ServiceQuote.ApprovalStatus.EXPIRED);
            serviceQuoteRepository.save(quote);
            log.info("Marked quote {} as expired", quote.getId());
        }
    }

    private void validateQuoteForCreation(ServiceQuote quote) {
        if (quote.getRequest() == null) {
            throw new IllegalArgumentException("Service request is required");
        }

        if (quote.getMechanic() == null) {
            throw new IllegalArgumentException("Mechanic is required");
        }

        if (quote.getTotalAmount() == null || quote.getTotalAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total amount is required and must be non-negative");
        }

        // Validate that the service request is in the correct status
        ServiceRequest request = quote.getRequest();
        if (request.getStatus() != ServiceRequest.RequestStatus.PENDING_QUOTE) {
            throw new IllegalArgumentException("Service request must be in PENDING_QUOTE status to create a quote");
        }
    }
}