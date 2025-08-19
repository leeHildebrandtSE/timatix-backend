package com.timatix.servicebooking.repository;

import com.timatix.bookingservices.model.BookingSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingSlotRepository extends JpaRepository<BookingSlot, Long> {
    List<BookingSlot> findByDate(LocalDate date);
}