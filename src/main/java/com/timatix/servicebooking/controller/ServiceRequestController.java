package com.timatix.servicebooking.controller;

import com.timatix.servicebooking.model.ServiceRequest;
import com.timatix.servicebooking.service.ServiceRequestService;
import com.timatix.servicebooking.dto.ServiceRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<ServiceRequest>> getAllServiceRequests() {
        List<ServiceRequest> requests = serviceRequestService.getAllServiceRequests();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequest> getServiceRequestById(@PathVariable Long id) {
        Optional<ServiceRequest> request = serviceRequestService.getServiceRequestById(id);
        return request.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ServiceRequest>> getServiceRequestsByClient(@PathVariable Long clientId) {
        List<ServiceRequest> requests = serviceRequestService.getServiceRequestsByClient(clientId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/mechanic/{mechanicId}")
    public ResponseEntity<List<ServiceRequest>> getServiceRequestsByMechanic(@PathVariable Long mechanicId) {
        List<ServiceRequest> requests = serviceRequestService.getServiceRequestsByMechanic(mechanicId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ServiceRequest>> getServiceRequestsByStatus(@PathVariable ServiceRequest.RequestStatus status) {
        List<ServiceRequest> requests = serviceRequestService.getServiceRequestsByStatus(status);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/pending-quotes")
    public ResponseEntity<List<ServiceRequest>> getPendingQuotes() {
        List<ServiceRequest> requests = serviceRequestService.getPendingQuotes();
        return ResponseEntity.ok(requests);
    }

    @PostMapping
    public ResponseEntity<?> createServiceRequest(@Valid @RequestBody ServiceRequestDto requestDto) {
        try {
            ServiceRequest request = serviceRequestService.createServiceRequest(
                    requestDto.getClientId(),
                    requestDto.getVehicleId(),
                    requestDto.getServiceId(),
                    convertDtoToEntity(requestDto)
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(request);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error creating service request", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateServiceRequest(@PathVariable Long id, @RequestBody ServiceRequest requestDetails) {
        try {
            ServiceRequest updatedRequest = serviceRequestService.updateServiceRequest(id, requestDetails);
            return ResponseEntity.ok(updatedRequest);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating service request", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}/assign-mechanic/{mechanicId}")
    public ResponseEntity<?> assignMechanic(@PathVariable Long id, @PathVariable Long mechanicId) {
        try {
            ServiceRequest updatedRequest = serviceRequestService.assignMechanic(id, mechanicId);
            return ResponseEntity.ok(updatedRequest);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error assigning mechanic", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        try {
            ServiceRequest.RequestStatus status = ServiceRequest.RequestStatus.valueOf(statusUpdate.get("status"));
            ServiceRequest updatedRequest = serviceRequestService.updateStatus(id, status);
            return ResponseEntity.ok(updatedRequest);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid status: " + statusUpdate.get("status"));
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating status", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteServiceRequest(@PathVariable Long id) {
        try {
            serviceRequestService.deleteServiceRequest(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Service request deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting service request", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
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