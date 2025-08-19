package com.timatix.servicebooking.repository;

import com.timatix.servicebooking.model.ServiceRequest;
import com.timatix.servicebooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByClient(User client);

    List<ServiceRequest> findByClientId(Long clientId);

    List<ServiceRequest> findByAssignedMechanicId(Long mechanicId);

    List<ServiceRequest> findByStatus(ServiceRequest.RequestStatus status);

    List<ServiceRequest> findByVehicleId(Long vehicleId);

    List<ServiceRequest> findByServiceId(Long serviceId);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.preferredDate = :date")
    List<ServiceRequest> findByPreferredDate(@Param("date") LocalDate date);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.preferredDate BETWEEN :startDate AND :endDate")
    List<ServiceRequest> findByPreferredDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.client.id = :clientId AND sr.status = :status")
    List<ServiceRequest> findByClientIdAndStatus(@Param("clientId") Long clientId, @Param("status") ServiceRequest.RequestStatus status);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.assignedMechanic.id = :mechanicId AND sr.status = :status")
    List<ServiceRequest> findByMechanicIdAndStatus(@Param("mechanicId") Long mechanicId, @Param("status") ServiceRequest.RequestStatus status);

    @Query("SELECT COUNT(sr) FROM ServiceRequest sr WHERE sr.assignedMechanic.id = :mechanicId AND sr.status IN ('BOOKING_CONFIRMED', 'IN_PROGRESS')")
    Long countActiveRequestsByMechanic(@Param("mechanicId") Long mechanicId);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.status = 'PENDING_QUOTE' ORDER BY sr.createdAt ASC")
    List<ServiceRequest> findPendingQuotesOrderByCreatedAt();
}