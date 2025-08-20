package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.ServiceRequest;
import com.timatix.servicebooking.model.User;
import com.timatix.servicebooking.repository.ServiceRequestRepository;
import com.timatix.servicebooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    public boolean isRequestOwner(Long requestId, String userEmail) {
        Optional<ServiceRequest> request = serviceRequestRepository.findById(requestId);
        if (request.isEmpty()) {
            return false;
        }

        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isEmpty()) {
            return false;
        }

        // Client can access their own requests, mechanics can access assigned requests
        ServiceRequest serviceRequest = request.get();
        User currentUser = user.get();

        return serviceRequest.getClient().getId().equals(currentUser.getId()) ||
                (serviceRequest.getAssignedMechanic() != null &&
                        serviceRequest.getAssignedMechanic().getId().equals(currentUser.getId()));
    }

    public boolean canAccessVehicle(Long vehicleId, String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isEmpty()) {
            return false;
        }

        // Check if user is admin, or if it's their vehicle
        User currentUser = user.get();
        return currentUser.getRole() == User.Role.ADMIN ||
                currentUser.getVehicles().stream()
                        .anyMatch(vehicle -> vehicle.getId().equals(vehicleId));
    }
}