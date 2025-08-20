package com.timatix.servicebooking.repository;

import com.timatix.servicebooking.model.ServiceQuote;
import com.timatix.servicebooking.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceQuoteRepository extends JpaRepository<ServiceQuote, Long> {

    Optional<ServiceQuote> findByRequest(ServiceRequest request);

    List<ServiceQuote> findByMechanicId(Long mechanicId);

    List<ServiceQuote> findByApprovalStatus(ServiceQuote.ApprovalStatus status);

    @Query("SELECT sq FROM ServiceQuote sq WHERE sq.validUntil < :currentTime AND sq.approvalStatus = 'PENDING'")
    List<ServiceQuote> findExpiredPendingQuotes(@Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT sq FROM ServiceQuote sq WHERE sq.request.client.id = :clientId")
    List<ServiceQuote> findByClientId(@Param("clientId") Long clientId);

    @Query("SELECT sq FROM ServiceQuote sq WHERE sq.mechanic.id = :mechanicId AND sq.approvalStatus = :status")
    List<ServiceQuote> findByMechanicIdAndStatus(@Param("mechanicId") Long mechanicId, @Param("status") ServiceQuote.ApprovalStatus status);

    @Query("SELECT sq FROM ServiceQuote sq WHERE sq.approvalStatus = 'PENDING'")
    List<ServiceQuote> findPendingQuotes();
}