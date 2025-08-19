package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.ServiceRequest;
import com.timatix.servicebooking.model.User;
import com.timatix.servicebooking.model.Vehicle;
import com.timatix.servicebooking.model.ServiceCatalog;
import com.timatix.servicebooking.repository.ServiceRequestRepository;
import com.timatix.servicebooking.repository.UserRepository;
import com.timatix.servicebooking.repository.VehicleRepository;
import com.timatix.servicebooking.repository.ServiceCatalogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceCatalogRepository serviceCatalogRepository;

    public List<ServiceRequest> getAllServiceRequests() {
        return serviceRequestRepository.findAll();
    }

    public Optional<ServiceRequest> getServiceRequestById(Long id) {
        return serviceRequestRepository.findById(id);
    }

    public List<ServiceRequest> getServiceRequestsByClient(Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + clientId));
        return serviceRequestRepository.findByClient(client);
    }

    public List<ServiceRequest> getServiceRequestsByMechanic(Long mechanicId) {
        return serviceRequestRepository.findByAssignedMechanicId(mechanicId);
    }

    public List<ServiceRequest> getServiceRequestsByStatus(ServiceRequest.RequestStatus status) {
        return serviceRequestRepository.findByStatus(status);
    }

    public List<ServiceRequest> getPendingQuotes() {
        return serviceRequestRepository.findByStatus(ServiceRequest.RequestStatus.PENDING_QUOTE);
    }

    public ServiceRequest createServiceRequest(ServiceRequest request) {
        validateServiceRequestForCreation(request);

        // Set initial status
        request.setStatus(ServiceRequest.RequestStatus.PENDING_QUOTE);

        log.info("Creating new service request for client ID: {} and vehicle ID: {}",
                request.getClient().getId(), request.getVehicle().getId());

        return serviceRequestRepository.save(request);
    }

    public ServiceRequest createServiceRequest(Long clientId, Long vehicleId, Long serviceId, ServiceRequest requestDetails) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + clientId));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));

        ServiceCatalog service = serviceCatalogRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + serviceId));

        // Verify that the vehicle belongs to the client
        if (!vehicle.getOwner().getId().equals(clientId)) {
            throw new IllegalArgumentException("Vehicle does not belong to the specified client");
        }

        ServiceRequest request = new ServiceRequest();
        request.setClient(client);
        request.setVehicle(vehicle);
        request.setService(service);
        request.setPreferredDate(requestDetails.getPreferredDate());
        request.setPreferredTime(requestDetails.getPreferredTime());
        request.setNotes(requestDetails.getNotes());
        request.setPhotoUrl(requestDetails.getPhotoUrl());

        return createServiceRequest(request);
    }

    public ServiceRequest updateServiceRequest(Long id, ServiceRequest requestDetails) {
        ServiceRequest request = serviceRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service request not found with id: " + id));

        // Update allowed fields
        request.setPreferredDate(requestDetails.getPreferredDate());
        request.setPreferredTime(requestDetails.getPreferredTime());
        request.setNotes(requestDetails.getNotes());
        request.setPhotoUrl(requestDetails.getPhotoUrl());

        if (requestDetails.getStatus() != null) {
            request.setStatus(requestDetails.getStatus());
        }

        log.info("Updating service request with id: {}", id);
        return serviceRequestRepository.save(request);
    }

    public ServiceRequest assignMechanic(Long requestId, Long mechanicId) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found with id: " + requestId));

        User mechanic = userRepository.findById(mechanicId)
                .orElseThrow(() -> new RuntimeException("Mechanic not found with id: " + mechanicId));

        if (mechanic.getRole() != User.Role.MECHANIC && mechanic.getRole() != User.Role.ADMIN) {
            throw new IllegalArgumentException("User is not a mechanic");
        }

        request.setAssignedMechanic(mechanic);

        log.info("Assigning mechanic {} to service request {}", mechanicId, requestId);
        return serviceRequestRepository.save(request);
    }

    public ServiceRequest updateStatus(Long requestId, ServiceRequest.RequestStatus status) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found with id: " + requestId));

        request.setStatus(status);

        log.info("Updating service request {} status to {}", requestId, status);
        return serviceRequestRepository.save(request);
    }

    public void deleteServiceRequest(Long id) {
        ServiceRequest request = serviceRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service request not found with id: " + id));

        // Only allow deletion if not yet confirmed
        if (request.getStatus() == ServiceRequest.RequestStatus.BOOKING_CONFIRMED ||
                request.getStatus() == ServiceRequest.RequestStatus.IN_PROGRESS ||
                request.getStatus() == ServiceRequest.RequestStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot delete service request that is confirmed or in progress");
        }

        log.info("Deleting service request with id: {}", id);
        serviceRequestRepository.deleteById(id);
    }

    private void validateServiceRequestForCreation(ServiceRequest request) {
        if (request.getClient() == null) {
            throw new IllegalArgumentException("Client is required");
        }

        if (request.getVehicle() == null) {
            throw new IllegalArgumentException("Vehicle is required");
        }

        if (request.getService() == null) {
            throw new IllegalArgumentException("Service is required");
        }

        if (request.getPreferredDate() == null) {
            throw new IllegalArgumentException("Preferred date is required");
        }

        if (request.getPreferredDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Preferred date cannot be in the past");
        }

        // Verify that the vehicle belongs to the client
        if (!request.getVehicle().getOwner().getId().equals(request.getClient().getId())) {
            throw new IllegalArgumentException("Vehicle does not belong to the specified client");
        }
    }
}