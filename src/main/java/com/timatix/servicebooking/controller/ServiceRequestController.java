package com.timatix.servicebooking.controller;

import com.timatix.bookingservices.model.ServiceRequest;
import com.timatix.bookingservices.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @GetMapping
    public List<ServiceRequest> getAllServiceRequests() {
        return serviceRequestRepository.findAll();
    }

    @PostMapping
    public ServiceRequest createServiceRequest(@RequestBody ServiceRequest request) {
        return serviceRequestRepository.save(request);
    }
}