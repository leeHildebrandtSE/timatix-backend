package com.timatix.servicebooking.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking_slots",
        uniqueConstraints = @UniqueConstraint(columnNames = {"date", "time_slot"}))
public class BookingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "time_slot", nullable = false)
    private LocalTime timeSlot;

    @Column(name = "max_bookings", nullable = false)
    private Integer maxBookings = 1;

    @Column(name = "current_bookings", nullable = false)
    private Integer currentBookings = 0;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // Auto-calculate availability based on bookings
        isAvailable = currentBookings < maxBookings;
    }

    public boolean hasAvailability() {
        return isAvailable && currentBookings < maxBookings;
    }

    public void incrementBookings() {
        if (hasAvailability()) {
            currentBookings++;
            if (currentBookings >= maxBookings) {
                isAvailable = false;
            }
        }
    }

    public void decrementBookings() {
        if (currentBookings > 0) {
            currentBookings--;
            isAvailable = true;
        }
    }
}