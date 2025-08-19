package com.timatix.servicebooking.repository;

import com.timatix.servicebooking.model.BookingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingSlotRepository extends JpaRepository<BookingSlot, Long> {

    List<BookingSlot> findByDate(LocalDate date);

    List<BookingSlot> findByIsAvailableTrue();

    List<BookingSlot> findByDateAndIsAvailableTrue(LocalDate date);

    List<BookingSlot> findByDateGreaterThanEqualAndIsAvailableTrue(LocalDate date);

    Optional<BookingSlot> findByDateAndTimeSlot(LocalDate date, LocalTime timeSlot);

    @Query("SELECT bs FROM BookingSlot bs WHERE bs.date BETWEEN :startDate AND :endDate")
    List<BookingSlot> findByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT bs FROM BookingSlot bs WHERE bs.date BETWEEN :startDate AND :endDate AND bs.isAvailable = true")
    List<BookingSlot> findAvailableSlotsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}