package com.timatix.servicebooking.controller;

import com.timatix.servicebooking.model.ServiceQuote;
import com.timatix.servicebooking.service.ServiceQuoteService;
import com.timatix.servicebooking.dto.ServiceQuoteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/service-quotes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServiceQuoteController {

    private final ServiceQuoteService serviceQuoteService;

    @GetMapping
    public ResponseEntity<List<ServiceQuote>> getAllQuotes() {
        List<ServiceQuote> quotes = serviceQuoteService.getAllQuotes();
        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceQuote> getQuoteById(@PathVariable Long id) {
        Optional<ServiceQuote> quote = serviceQuoteService.getQuoteById(id);
        return quote.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<ServiceQuote> getQuoteByRequestId(@PathVariable Long requestId) {
        Optional<ServiceQuote> quote = serviceQuoteService.getQuoteByRequestId(requestId);
        return quote.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mechanic/{mechanicId}")
    public ResponseEntity<List<ServiceQuote>> getQuotesByMechanic(@PathVariable Long mechanicId) {
        List<ServiceQuote> quotes = serviceQuoteService.getQuotesByMechanic(mechanicId);
        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ServiceQuote>> getQuotesByStatus(@PathVariable ServiceQuote.ApprovalStatus status) {
        List<ServiceQuote> quotes = serviceQuoteService.getQuotesByStatus(status);
        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ServiceQuote>> getPendingQuotes() {
        List<ServiceQuote> quotes = serviceQuoteService.getPendingQuotes();
        return ResponseEntity.ok(quotes);
    }

    @PostMapping
    public ResponseEntity<?> createQuote(@Valid @RequestBody ServiceQuote quote) {
        try {
            ServiceQuote savedQuote = serviceQuoteService.createQuote(quote);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedQuote);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error creating service quote", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/request/{requestId}/mechanic/{mechanicId}")
    public ResponseEntity<?> createQuoteForRequest(
            @PathVariable Long requestId,
            @PathVariable Long mechanicId,
            @Valid @RequestBody ServiceQuoteDto quoteDto) {
        try {
            ServiceQuote quoteDetails = convertDtoToEntity(quoteDto);
            ServiceQuote savedQuote = serviceQuoteService.createQuoteForRequest(requestId, mechanicId, quoteDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedQuote);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error creating service quote for request", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuote(@PathVariable Long id, @RequestBody ServiceQuote quoteDetails) {
        try {
            ServiceQuote updatedQuote = serviceQuoteService.updateQuote(id, quoteDetails);
            return ResponseEntity.ok(updatedQuote);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating service quote", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveQuote(@PathVariable Long id) {
        try {
            ServiceQuote approvedQuote = serviceQuoteService.approveQuote(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Quote approved successfully");
            response.put("quote", approvedQuote);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error approving service quote", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}/decline")
    public ResponseEntity<?> declineQuote(@PathVariable Long id) {
        try {
            ServiceQuote declinedQuote = serviceQuoteService.declineQuote(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Quote declined successfully");
            response.put("quote", declinedQuote);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error declining service quote", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuote(@PathVariable Long id) {
        try {
            serviceQuoteService.deleteQuote(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Service quote deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting service quote", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/mark-expired")
    public ResponseEntity<?> markExpiredQuotes() {
        try {
            serviceQuoteService.markExpiredQuotes();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Expired quotes marked successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error marking expired quotes", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    private ServiceQuote convertDtoToEntity(ServiceQuoteDto dto) {
        ServiceQuote quote = new ServiceQuote();
        quote.setLineItemsJson(dto.getLineItemsJson());
        quote.setLabourCost(dto.getLabourCost());
        quote.setPartsCost(dto.getPartsCost());
        quote.setTotalAmount(dto.getTotalAmount());
        quote.setNotes(dto.getNotes());
        quote.setValidUntil(dto.getValidUntil());
        return quote;
    }
}