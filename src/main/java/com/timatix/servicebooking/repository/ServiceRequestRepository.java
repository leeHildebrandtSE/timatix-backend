package com.timatix.servicebooking.repository;

import com.timatix.bookingservices.model.ServiceRequest;
import com.timatix.bookingservices.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByClient(User client);
}