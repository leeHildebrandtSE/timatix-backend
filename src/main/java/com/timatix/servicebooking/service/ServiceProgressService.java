package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.ServiceProgress;
import com.timatix.servicebooking.model.ServiceRequest;
import com.timatix.servicebooking.model.User;
import com.timatix.servicebooking.repository.ServiceProgressRepository;
import com.timatix.servicebooking.repository.ServiceRequestRepository;
import com.timatix.servicebooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ServiceProgressService {

    private final ServiceProgressRepository serviceProgressRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ServiceProgress addProgressUpdate(Long serviceRequestId, Long mechanicId,
                                             ServiceProgress.ProgressPhase phase,
                                             String comment, String photoUrl) {

        ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        User mechanic = userRepository.findById(mechanicId)
                .orElseThrow(() -> new RuntimeException("Mechanic not found"));

        ServiceProgress progress = new ServiceProgress();
        progress.setServiceRequest(serviceRequest);
        progress.setUpdatedBy(mechanic);
        progress.setPhase(phase);
        progress.setComment(comment);
        progress.setPhotoUrl(photoUrl);

        // Set estimated completion based on phase
        if (phase == ServiceProgress.ProgressPhase.DIAGNOSIS) {
            progress.setEstimatedCompletion(LocalDateTime.now().plusHours(2));
        } else if (phase == ServiceProgress.ProgressPhase.REPAIR_IN_PROGRESS) {
            progress.setEstimatedCompletion(LocalDateTime.now().plusDays(1));
        } else if (phase == ServiceProgress.ProgressPhase.READY_FOR_COLLECTION) {
            progress.setEstimatedCompletion(LocalDateTime.now());
            // Update service request status
            serviceRequest.setStatus(ServiceRequest.RequestStatus.COMPLETED);
            serviceRequestRepository.save(serviceRequest);
        }

        ServiceProgress savedProgress = serviceProgressRepository.save(progress);

        // Send notification to client
        log.info("Progress update added for service request {}: {}", serviceRequestId, phase);

        return savedProgress;
    }

    public List<ServiceProgress> getProgressByServiceRequest(Long serviceRequestId) {
        return serviceProgressRepository.findByServiceRequestIdOrderByCreatedAtDesc(serviceRequestId);
    }
}