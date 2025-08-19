package com.timatix.servicebooking.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class ServiceRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User client;

    @ManyToOne
    private Vehicle vehicle;

    @ManyToOne
    private ServiceCatalog service;

    private LocalDate preferredDate;
    private LocalTime preferredTime;
    private String notes;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public enum RequestStatus {
        PENDING_QUOTE, QUOTE_SENT, APPROVED, DECLINED, CONFIRMED
    }

    // Getters and Setters
}