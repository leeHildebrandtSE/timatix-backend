package com.timatix.servicebooking.controller;

import com.timatix.servicebooking.model.ServiceProgress;
import com.timatix.servicebooking.service.ServiceProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/service-progress")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServiceProgressController {

    private final ServiceProgressService serviceProgressService;

    @PostMapping("/update")
    @PreAuthorize("hasRole('MECHANIC') or hasRole('ADMIN')")
    public ResponseEntity<?> addProgressUpdate(@RequestBody Map<String, Object> request) {
        try {
            Long serviceRequestId = Long.valueOf(request.get("serviceRequestId").toString());
            Long mechanicId = Long.valueOf(request.get("mechanicId").toString());
            ServiceProgress.ProgressPhase phase = ServiceProgress.ProgressPhase.valueOf(request.get("phase").toString());
            String comment = (String) request.get("comment");
            String photoUrl = (String) request.get("photoUrl");

            ServiceProgress progress = serviceProgressService.addProgressUpdate(
                    serviceRequestId, mechanicId, phase, comment, photoUrl);

            return ResponseEntity.status(HttpStatus.CREATED).body(progress);
        } catch (Exception e) {
            log.error("Error adding progress update", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/service-request/{serviceRequestId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MECHANIC') or @serviceRequestService.isRequestOwner(#serviceRequestId, authentication.principal.username)")
    public ResponseEntity<List<ServiceProgress>> getProgressByServiceRequest(@PathVariable Long serviceRequestId) {
        List<ServiceProgress> progress = serviceProgressService.getProgressByServiceRequest(serviceRequestId);
        return ResponseEntity.ok(progress);
    }
}