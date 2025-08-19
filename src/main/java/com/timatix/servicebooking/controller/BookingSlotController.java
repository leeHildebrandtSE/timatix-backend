package com.timatix.servicebooking.controller;

import com.timatix.servicebooking.model.BookingSlot;
import com.timatix.servicebooking.service.BookingSlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/booking-slots")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingSlotController {

    private final BookingSlotService bookingSlotService;

    @GetMapping
    public ResponseEntity<List<BookingSlot>> getAllSlots() {
        List<BookingSlot> slots = bookingSlotService.getAllSlots();
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingSlot> getSlotById(@PathVariable Long id) {
        Optional<BookingSlot> slot = bookingSlotService.getSlotById(id);
        return slot.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<BookingSlot>> getSlotsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<BookingSlot> slots = bookingSlotService.getSlotsByDate(date);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/available")
    public ResponseEntity<List<BookingSlot>> getAvailableSlots() {
        List<BookingSlot> slots = bookingSlotService.getAvailableSlots();
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/available/date/{date}")
    public ResponseEntity<List<BookingSlot>> getAvailableSlotsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<BookingSlot> slots = bookingSlotService.getAvailableSlotsByDate(date);
        return ResponseEntity.ok(slots);
    }

    @PostMapping
    public ResponseEntity<?> createSlot(@RequestBody BookingSlot slot) {
        try {
            BookingSlot savedSlot = bookingSlotService.createSlot(slot);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSlot);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error creating booking slot", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSlot(@PathVariable Long id, @RequestBody BookingSlot slotDetails) {
        try {
            BookingSlot updatedSlot = bookingSlotService.updateSlot(id, slotDetails);
            return ResponseEntity.ok(updatedSlot);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating booking slot", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSlot(@PathVariable Long id) {
        try {
            bookingSlotService.deleteSlot(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Booking slot deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting booking slot", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}