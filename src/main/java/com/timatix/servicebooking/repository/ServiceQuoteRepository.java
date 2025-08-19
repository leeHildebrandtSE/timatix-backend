package com.timatix.servicebooking.repository;

import com.timatix.bookingservices.model.ServiceQuote;
import com.timatix.bookingservices.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceQuoteRepository extends JpaRepository<ServiceQuote, Long> {
    Optional<ServiceQuote> findByRequest(ServiceRequest request);
}