package com.timatix.servicebooking.repository;

import com.timatix.servicebooking.model.ServiceProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceProgressRepository extends JpaRepository<ServiceProgress, Long> {

    List<ServiceProgress> findByServiceRequestIdOrderByCreatedAtDesc(Long serviceRequestId);

    List<ServiceProgress> findByUpdatedByIdOrderByCreatedAtDesc(Long mechanicId);

    @Query("SELECT sp FROM ServiceProgress sp WHERE sp.serviceRequest.id = :serviceRequestId AND sp.phase = :phase")
    List<ServiceProgress> findByServiceRequestIdAndPhase(@Param("serviceRequestId") Long serviceRequestId,
                                                         @Param("phase") ServiceProgress.ProgressPhase phase);

    @Query("SELECT sp FROM ServiceProgress sp WHERE sp.serviceRequest.client.id = :clientId ORDER BY sp.createdAt DESC")
    List<ServiceProgress> findByClientIdOrderByCreatedAtDesc(@Param("clientId") Long clientId);
}