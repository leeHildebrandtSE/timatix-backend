package com.timatix.servicebooking.service;

import com.timatix.servicebooking.repository.BookingSlotRepository;
import com.timatix.servicebooking.repository.ServiceQuoteRepository;
import com.timatix.servicebooking.repository.ServiceRequestRepository;
import com.timatix.servicebooking.repository.UserRepository;
import com.timatix.servicebooking.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessMetricsService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final ServiceQuoteRepository serviceQuoteRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final BookingSlotRepository bookingSlotRepository;

    public Map<String, Object> getDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // Basic counts
        metrics.put("totalClients", userRepository.findAllClients().size());
        metrics.put("totalMechanics", userRepository.findAllMechanics().size());
        metrics.put("totalVehicles", vehicleRepository.count());
        metrics.put("totalServiceRequests", serviceRequestRepository.count());

        // Today's metrics
        LocalDate today = LocalDate.now();
        metrics.put("todayBookings", bookingSlotRepository.countAvailableSlotsByDate(today));
        metrics.put("pendingQuotes", serviceQuoteRepository.findByApprovalStatus(
                com.timatix.servicebooking.model.ServiceQuote.ApprovalStatus.PENDING).size());

        // Status breakdown
        Map<String, Long> statusBreakdown = new HashMap<>();
        for (var status : com.timatix.servicebooking.model.ServiceRequest.RequestStatus.values()) {
            long count = serviceRequestRepository.findByStatus(status).size();
            statusBreakdown.put(status.name(), count);
        }
        metrics.put("requestStatusBreakdown", statusBreakdown);

        return metrics;
    }

    public Map<String, Object> getMechanicMetrics(Long mechanicId) {
        Map<String, Object> metrics = new HashMap<>();

        metrics.put("assignedRequests", serviceRequestRepository.findByAssignedMechanicId(mechanicId).size());
        metrics.put("activeRequests", serviceRequestRepository.countActiveRequestsByMechanic(mechanicId));
        metrics.put("quotesCreated", serviceQuoteRepository.findByMechanicId(mechanicId).size());

        return metrics;
    }
}