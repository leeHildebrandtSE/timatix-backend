// Enhanced Service Request Controller with Notifications
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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC')")
    public ResponseEntity<List<ServiceRequest>> getAllServiceRequests() {
        List<ServiceRequest> requests = serviceRequestService.getAllServiceRequests();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC') or @serviceRequestService.isRequestOwner(#id, authentication.principal.username)")
    public ResponseEntity<ServiceRequest> getServiceRequestById(@PathVariable Long id) {
        Optional<ServiceRequest> request = serviceRequestService.getServiceRequestById(id);
        return request.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasRole('ADMIN') or #clientId == authentication.principal.id")
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

            // Send notification and log audit
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

            // Send notification and log audit
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

            // Send notification and log audit
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