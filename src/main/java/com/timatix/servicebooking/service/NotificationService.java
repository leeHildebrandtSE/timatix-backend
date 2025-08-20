package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.ServiceQuote;
import com.timatix.servicebooking.model.ServiceRequest;
import com.timatix.servicebooking.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    // In a real implementation, you would inject email/SMS services here
    // private final EmailService emailService;
    // private final SmsService smsService;

    public void notifyQuoteCreated(ServiceQuote quote) {
        User client = quote.getRequest().getClient();
        String message = String.format(
                "Hello %s, you have received a quote for your %s %s service request. " +
                        "Quote amount: R%.2f. Please review and approve in the app.",
                client.getName(),
                quote.getRequest().getVehicle().getMake(),
                quote.getRequest().getVehicle().getModel(),
                quote.getTotalAmount()
        );

        log.info("Sending quote notification to client: {}", client.getEmail());
        // emailService.sendEmail(client.getEmail(), "Service Quote Ready", message);

        // For now, just log the notification
        log.info("NOTIFICATION - Quote Created: {}", message);
    }

    public void notifyQuoteApproved(ServiceQuote quote) {
        User mechanic = quote.getMechanic();
        String message = String.format(
                "Hello %s, your quote for %s %s has been approved by the client. " +
                        "Please prepare for the service on %s.",
                mechanic.getName(),
                quote.getRequest().getVehicle().getMake(),
                quote.getRequest().getVehicle().getModel(),
                quote.getRequest().getPreferredDate()
        );

        log.info("Sending quote approval notification to mechanic: {}", mechanic.getEmail());
        log.info("NOTIFICATION - Quote Approved: {}", message);
    }

    public void notifyServiceRequestCreated(ServiceRequest request) {
        String message = String.format(
                "New service request received for %s %s. Service: %s. Preferred date: %s",
                request.getVehicle().getMake(),
                request.getVehicle().getModel(),
                request.getService().getName(),
                request.getPreferredDate()
        );

        log.info("NOTIFICATION - New Service Request: {}", message);
    }

    public void notifyMechanicAssigned(ServiceRequest request) {
        User mechanic = request.getAssignedMechanic();
        if (mechanic != null) {
            String message = String.format(
                    "Hello %s, you have been assigned to a new service request for %s %s. " +
                            "Service: %s. Client: %s",
                    mechanic.getName(),
                    request.getVehicle().getMake(),
                    request.getVehicle().getModel(),
                    request.getService().getName(),
                    request.getClient().getName()
            );

            log.info("NOTIFICATION - Mechanic Assigned: {}", message);
        }
    }

    public void notifyServiceStatusUpdate(ServiceRequest request, ServiceRequest.RequestStatus newStatus) {
        User client = request.getClient();
        String statusMessage = getStatusMessage(newStatus);

        String message = String.format(
                "Hello %s, your service request for %s %s has been updated. Status: %s",
                client.getName(),
                request.getVehicle().getMake(),
                request.getVehicle().getModel(),
                statusMessage
        );

        log.info("NOTIFICATION - Service Status Update: {}", message);
    }

    private String getStatusMessage(ServiceRequest.RequestStatus status) {
        return switch (status) {
            case PENDING_QUOTE -> "Waiting for quote";
            case QUOTE_SENT -> "Quote ready for review";
            case QUOTE_APPROVED -> "Quote approved, booking confirmed";
            case QUOTE_DECLINED -> "Quote declined";
            case BOOKING_CONFIRMED -> "Service booking confirmed";
            case IN_PROGRESS -> "Service in progress";
            case COMPLETED -> "Service completed";
            case CANCELLED -> "Service cancelled";
        };
    }
}