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

    /**
     * Find all booking slots for a specific date
     */
    List<BookingSlot> findByDate(LocalDate date);

    /**
     * Find all available booking slots
     */
    List<BookingSlot> findByIsAvailableTrue();

    /**
     * Find available booking slots for a specific date
     */
    List<BookingSlot> findByDateAndIsAvailableTrue(LocalDate date);

    /**
     * Find available booking slots from a specific date onwards
     */
    List<BookingSlot> findByDateGreaterThanEqualAndIsAvailableTrue(LocalDate date);

    /**
     * Find a specific booking slot by date and time
     */
    Optional<BookingSlot> findByDateAndTimeSlot(LocalDate date, LocalTime timeSlot);

    /**
     * Find booking slots within a date range
     */
    @Query("SELECT bs FROM BookingSlot bs WHERE bs.date BETWEEN :startDate AND :endDate ORDER BY bs.date, bs.timeSlot")
    List<BookingSlot> findByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find available booking slots within a date range
     */
    @Query("SELECT bs FROM BookingSlot bs WHERE bs.date BETWEEN :startDate AND :endDate AND bs.isAvailable = true ORDER BY bs.date, bs.timeSlot")
    List<BookingSlot> findAvailableSlotsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find booking slots with availability (current bookings < max bookings)
     */
    @Query("SELECT bs FROM BookingSlot bs WHERE bs.currentBookings < bs.maxBookings AND bs.date >= CURRENT_DATE ORDER BY bs.date, bs.timeSlot")
    List<BookingSlot> findSlotsWithAvailability();

    /**
     * Count total available slots for a date
     */
    @Query("SELECT COUNT(bs) FROM BookingSlot bs WHERE bs.date = :date AND bs.isAvailable = true")
    Long countAvailableSlotsByDate(@Param("date") LocalDate date);

    /**
     * Find booking slots by max bookings capacity
     */
    List<BookingSlot> findByMaxBookings(Integer maxBookings);

    /**
     * Find fully booked slots (current bookings >= max bookings)
     */
    @Query("SELECT bs FROM BookingSlot bs WHERE bs.currentBookings >= bs.maxBookings")
    List<BookingSlot> findFullyBookedSlots();
}