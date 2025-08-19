package com.timatix.servicebooking.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
public class ServiceQuote {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private ServiceRequest request;

    @ManyToOne
    private User mechanic;

    @Lob
    private String lineItemsJson;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    public enum ApprovalStatus {
        PENDING, ACCEPTED, DECLINED
    }

    // Getters and Setters
}