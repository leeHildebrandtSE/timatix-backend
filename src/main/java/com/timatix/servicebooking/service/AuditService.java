package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuditService {

    public void logUserAction(User user, String action, String details) {
        log.info("AUDIT - User: {} ({}), Action: {}, Details: {}, Timestamp: {}",
                user.getName(), user.getEmail(), action, details, LocalDateTime.now());
    }

    public void logServiceRequestAction(Long requestId, String action, String details) {
        log.info("AUDIT - Service Request: {}, Action: {}, Details: {}, Timestamp: {}",
                requestId, action, details, LocalDateTime.now());
    }

    public void logQuoteAction(Long quoteId, String action, String details) {
        log.info("AUDIT - Quote: {}, Action: {}, Details: {}, Timestamp: {}",
                quoteId, action, details, LocalDateTime.now());
    }

    public void logSystemAction(String action, String details) {
        log.info("AUDIT - System Action: {}, Details: {}, Timestamp: {}",
                action, details, LocalDateTime.now());
    }
}