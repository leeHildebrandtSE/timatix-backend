package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.BookingSlot;
import com.timatix.servicebooking.repository.BookingSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingSlotService {

    private final BookingSlotRepository bookingSlotRepository;

    public List<BookingSlot> getAllSlots() {
        log.debug("Fetching all booking slots");
        return bookingSlotRepository.findAll();
    }

    public Optional<BookingSlot> getSlotById(Long id) {
        log.debug("Fetching booking slot with id: {}", id);
        return bookingSlotRepository.findById(id);
    }

    public List<BookingSlot> getSlotsByDate(LocalDate date) {
        log.debug("Fetching booking slots for date: {}", date);
        return bookingSlotRepository.findByDate(date);
    }

    public List<BookingSlot> getAvailableSlots() {
        log.debug("Fetching all available booking slots");
        return bookingSlotRepository.findByIsAvailableTrue();
    }

    public List<BookingSlot> getAvailableSlotsByDate(LocalDate date) {
        log.debug("Fetching available booking slots for date: {}", date);
        return bookingSlotRepository.findByDateAndIsAvailableTrue(date);
    }

    public List<BookingSlot> getUpcomingAvailableSlots() {
        LocalDate today = LocalDate.now();
        log.debug("Fetching upcoming available booking slots from: {}", today);
        return bookingSlotRepository.findByDateGreaterThanEqualAndIsAvailableTrue(today);
    }

    public BookingSlot createSlot(BookingSlot slot) {
        log.info("Creating new booking slot for date: {} at time: {}", slot.getDate(), slot.getTimeSlot());

        validateSlotForCreation(slot);

        // Check if slot already exists for this date and time
        Optional<BookingSlot> existingSlot = bookingSlotRepository.findByDateAndTimeSlot(slot.getDate(), slot.getTimeSlot());
        if (existingSlot.isPresent()) {
            throw new IllegalArgumentException("Booking slot already exists for this date and time");
        }

        // Set default values if not provided
        if (slot.getMaxBookings() == null) {
            slot.setMaxBookings(1);
        }
        if (slot.getCurrentBookings() == null) {
            slot.setCurrentBookings(0);
        }
        if (slot.getIsAvailable() == null) {
            slot.setIsAvailable(true);
        }

        BookingSlot savedSlot = bookingSlotRepository.save(slot);
        log.info("Successfully created booking slot with id: {}", savedSlot.getId());
        return savedSlot;
    }

    public BookingSlot updateSlot(Long id, BookingSlot slotDetails) {
        log.info("Updating booking slot with id: {}", id);

        BookingSlot slot = bookingSlotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking slot not found with id: " + id));

        // Update the slot details
        slot.setDate(slotDetails.getDate());
        slot.setTimeSlot(slotDetails.getTimeSlot());
        slot.setMaxBookings(slotDetails.getMaxBookings());

        if (slotDetails.getIsAvailable() != null) {
            slot.setIsAvailable(slotDetails.getIsAvailable());
        }

        // Recalculate availability based on current bookings vs max bookings
        slot.setIsAvailable(slot.getCurrentBookings() < slot.getMaxBookings());

        BookingSlot updatedSlot = bookingSlotRepository.save(slot);
        log.info("Successfully updated booking slot with id: {}", id);
        return updatedSlot;
    }

    public void deleteSlot(Long id) {
        log.info("Deleting booking slot with id: {}", id);

        BookingSlot slot = bookingSlotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking slot not found with id: " + id));

        // Check if slot has active bookings before deletion
        if (slot.getCurrentBookings() > 0) {
            throw new IllegalArgumentException("Cannot delete booking slot that has active bookings");
        }

        bookingSlotRepository.deleteById(id);
        log.info("Successfully deleted booking slot with id: {}", id);
    }

    // Additional utility methods

    public BookingSlot bookSlot(Long slotId) {
        log.info("Booking slot with id: {}", slotId);

        BookingSlot slot = bookingSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Booking slot not found with id: " + slotId));

        if (!slot.hasAvailability()) {
            throw new IllegalArgumentException("Booking slot is not available");
        }

        slot.incrementBookings();
        BookingSlot updatedSlot = bookingSlotRepository.save(slot);
        log.info("Successfully booked slot {} - new booking count: {}", slotId, updatedSlot.getCurrentBookings());
        return updatedSlot;
    }

    public BookingSlot cancelBooking(Long slotId) {
        log.info("Cancelling booking for slot with id: {}", slotId);

        BookingSlot slot = bookingSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Booking slot not found with id: " + slotId));

        slot.decrementBookings();
        BookingSlot updatedSlot = bookingSlotRepository.save(slot);
        log.info("Successfully cancelled booking for slot {} - new booking count: {}", slotId, updatedSlot.getCurrentBookings());
        return updatedSlot;
    }

    public void generateSlotsForDateRange(LocalDate startDate, LocalDate endDate, List<LocalTime> timeSlots, Integer maxBookings) {
        log.info("Generating booking slots from {} to {} for {} time slots", startDate, endDate, timeSlots.size());

        LocalDate currentDate = startDate;
        int slotsCreated = 0;

        while (!currentDate.isAfter(endDate)) {
            for (LocalTime timeSlot : timeSlots) {
                // Check if slot already exists
                Optional<BookingSlot> existingSlot = bookingSlotRepository.findByDateAndTimeSlot(currentDate, timeSlot);
                if (existingSlot.isEmpty()) {
                    BookingSlot slot = new BookingSlot();
                    slot.setDate(currentDate);
                    slot.setTimeSlot(timeSlot);
                    slot.setMaxBookings(maxBookings != null ? maxBookings : 1);
                    slot.setCurrentBookings(0);
                    slot.setIsAvailable(true);

                    bookingSlotRepository.save(slot);
                    slotsCreated++;
                    log.debug("Created slot for {} at {}", currentDate, timeSlot);
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        log.info("Successfully generated {} booking slots", slotsCreated);
    }

    // Helper method to generate weekday slots only (Monday to Friday)
    public void generateWeekdaySlotsForDateRange(LocalDate startDate, LocalDate endDate, List<LocalTime> timeSlots, Integer maxBookings) {
        log.info("Generating weekday booking slots from {} to {}", startDate, endDate);

        LocalDate currentDate = startDate;
        int slotsCreated = 0;

        while (!currentDate.isAfter(endDate)) {
            // Check if it's a weekday (Monday = 1, Sunday = 7)
            if (currentDate.getDayOfWeek().getValue() <= 5) {
                for (LocalTime timeSlot : timeSlots) {
                    Optional<BookingSlot> existingSlot = bookingSlotRepository.findByDateAndTimeSlot(currentDate, timeSlot);
                    if (existingSlot.isEmpty()) {
                        BookingSlot slot = new BookingSlot();
                        slot.setDate(currentDate);
                        slot.setTimeSlot(timeSlot);
                        slot.setMaxBookings(maxBookings != null ? maxBookings : 1);
                        slot.setCurrentBookings(0);
                        slot.setIsAvailable(true);

                        bookingSlotRepository.save(slot);
                        slotsCreated++;
                        log.debug("Created weekday slot for {} at {}", currentDate, timeSlot);
                    }
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        log.info("Successfully generated {} weekday booking slots", slotsCreated);
    }

    private void validateSlotForCreation(BookingSlot slot) {
        if (slot.getDate() == null) {
            throw new IllegalArgumentException("Booking date is required");
        }

        if (slot.getTimeSlot() == null) {
            throw new IllegalArgumentException("Time slot is required");
        }

        if (slot.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot create booking slot for past dates");
        }

        if (slot.getMaxBookings() != null && slot.getMaxBookings() <= 0) {
            throw new IllegalArgumentException("Max bookings must be greater than 0");
        }
    }
}