package com.timatix.servicebooking.controller;

import com.timatix.bookingservices.model.BookingSlot;
import com.timatix.bookingservices.repository.BookingSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking-slots")
public class BookingSlotController {

    @Autowired
    private BookingSlotRepository bookingSlotRepository;

    @GetMapping
    public List<BookingSlot> getAllSlots() {
        return bookingSlotRepository.findAll();
    }

    @PostMapping
    public BookingSlot createSlot(@RequestBody BookingSlot slot) {
        return bookingSlotRepository.save(slot);
    }
}