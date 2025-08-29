package com.timatix.servicebooking.controller;

import com.timatix.servicebooking.model.ServiceRequest;
import com.timatix.servicebooking.service.ServiceRequestService;
import com.timatix.servicebooking.service.NotificationService;
import com.timatix.servicebooking.service.AuditService;
import com.timatix.servicebooking.dto.ServiceRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/service-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;
    private final NotificationService notificationService;
    private final AuditService auditService;

    // Admin & Mechanic can see all requests
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC')")
    public ResponseEntity<List<ServiceRequest>> getAllServiceRequests() {
        List<ServiceRequest> requests = serviceRequestService.getAllServiceRequests();
        return ResponseEntity.ok(requests);
    }

    // Admin & Mechanic can get by ID; Client can get only their own request
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC') or @serviceRequestService.isRequestOwner(#requestId, authentication.principal.username)")
    public ResponseEntity<ServiceRequest> getServiceRequestById(@PathVariable("id") Long requestId) {
        Optional<ServiceRequest> request = serviceRequestService.getServiceRequestById(requestId);
        return request.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CLIENT gets their own requests without passing their ID explicitly
    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<ServiceRequest>> getMyServiceRequests(Authentication authentication) {
        Long userId = serviceRequestService.getUserIdFromPrincipal(authentication.getPrincipal());
        List<ServiceRequest> requests = serviceRequestService.getServiceRequestsByClient(userId);
        return ResponseEntity.ok(requests);
    }

    // Admin can fetch any client requests
    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ServiceRequest>> getServiceRequestsByClient(@PathVariable Long clientId) {
        List<ServiceRequest> requests = serviceRequestService.getServiceRequestsByClient(clientId);
        return ResponseEntity.ok(requests);
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> createServiceRequest(@Valid @RequestBody ServiceRequestDto requestDto) {
        try {
            ServiceRequest request = serviceRequestService.createServiceRequest(
                    requestDto.getClientId(),
                    requestDto.getVehicleId(),
                    requestDto.getServiceId(),
                    convertDtoToEntity(requestDto)
            );

            // Notifications & audit
            notificationService.notifyServiceRequestCreated(request);
            auditService.logServiceRequestAction(request.getId(), "CREATED",
                    "Service request created for " + request.getService().getName());

            return ResponseEntity.status(HttpStatus.CREATED).body(request);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error creating service request", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}/assign-mechanic/{mechanicId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignMechanic(@PathVariable Long id, @PathVariable Long mechanicId) {
        try {
            ServiceRequest updatedRequest = serviceRequestService.assignMechanic(id, mechanicId);

            notificationService.notifyMechanicAssigned(updatedRequest);
            auditService.logServiceRequestAction(id, "MECHANIC_ASSIGNED",
                    "Mechanic " + mechanicId + " assigned to request");

            return ResponseEntity.ok(updatedRequest);
        } catch (Exception e) {
            log.error("Error assigning mechanic", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC')")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        try {
            ServiceRequest.RequestStatus status = ServiceRequest.RequestStatus.valueOf(statusUpdate.get("status"));
            ServiceRequest updatedRequest = serviceRequestService.updateStatus(id, status);

            notificationService.notifyServiceStatusUpdate(updatedRequest, status);
            auditService.logServiceRequestAction(id, "STATUS_UPDATED", "Status changed to " + status);

            return ResponseEntity.ok(updatedRequest);
        } catch (Exception e) {
            log.error("Error updating status", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    private ServiceRequest convertDtoToEntity(ServiceRequestDto dto) {
        ServiceRequest request = new ServiceRequest();
        request.setPreferredDate(dto.getPreferredDate());
        request.setPreferredTime(dto.getPreferredTime());
        request.setNotes(dto.getNotes());
        request.setPhotoUrl(dto.getPhotoUrl());
        return request;
    }
}
