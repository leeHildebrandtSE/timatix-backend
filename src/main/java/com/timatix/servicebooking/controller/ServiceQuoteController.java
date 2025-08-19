package com.timatix.servicebooking.controller;

import com.timatix.servicebooking.model.ServiceQuote;
import com.timatix.servicebooking.repository.ServiceQuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-quotes")
public class ServiceQuoteController {

    @Autowired
    private ServiceQuoteRepository serviceQuoteRepository;

    @GetMapping
    public List<ServiceQuote> getAllQuotes() {
        return serviceQuoteRepository.findAll();
    }

    @PostMapping
    public ServiceQuote createQuote(@RequestBody ServiceQuote quote) {
        return serviceQuoteRepository.save(quote);
    }
}