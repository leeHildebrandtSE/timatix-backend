package com.timatix.servicebooking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_progress")
public class ServiceProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id", nullable = false)
    private ServiceRequest serviceRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id", nullable = false)
    private User updatedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgressPhase phase;

    @Column(length = 1000)
    private String comment;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "estimated_completion")
    private LocalDateTime estimatedCompletion;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum ProgressPhase {
        RECEIVED("Vehicle Received"),
        DIAGNOSIS("Diagnosis in Progress"),
        PARTS_ORDERED("Parts Ordered"),
        REPAIR_IN_PROGRESS("Repair in Progress"),
        QUALITY_CHECK("Quality Check"),
        CLEANING("Cleaning & Finishing"),
        READY_FOR_COLLECTION("Ready for Collection");

        private final String displayName;

        ProgressPhase(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}