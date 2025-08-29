package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.Invoice;
import com.timatix.servicebooking.model.Payment;
import com.timatix.servicebooking.model.ServiceRequest;
import com.timatix.servicebooking.repository.InvoiceRepository;
import com.timatix.servicebooking.repository.PaymentRepository;
import com.timatix.servicebooking.repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ScheduledTasksService {

    private final ServiceQuoteService serviceQuoteService;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    /**
     * Mark expired quotes every hour
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    public void markExpiredQuotes() {
        try {
            log.info("Running scheduled task: Mark expired quotes");
            serviceQuoteService.markExpiredQuotes();
        } catch (Exception e) {
            log.error("Error in markExpiredQuotes scheduled task", e);
        }
    }

    /**
     * Mark overdue invoices every day at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void markOverdueInvoices() {
        try {
            log.info("Running scheduled task: Mark overdue invoices");

            List<Invoice> unpaidInvoices = invoiceRepository.findByPaymentStatus(Invoice.PaymentStatus.UNPAID);
            LocalDateTime now = LocalDateTime.now();

            for (Invoice invoice : unpaidInvoices) {
                if (invoice.getDueDate() != null && invoice.getDueDate().isBefore(now)) {
                    invoice.setPaymentStatus(Invoice.PaymentStatus.OVERDUE);
                    invoiceRepository.save(invoice);

                    // Send overdue notification
                    sendOverdueNotification(invoice);
                    log.info("Marked invoice {} as overdue", invoice.getInvoiceNumber());
                }
            }
        } catch (Exception e) {
            log.error("Error in markOverdueInvoices scheduled task", e);
        }
    }

    /**
     * Clean up stale pending payments every 6 hours
     */
    @Scheduled(fixedRate = 21600000) // Every 6 hours
    public void cleanupStalePayments() {
        try {
            log.info("Running scheduled task: Cleanup stale payments");

            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
            List<Payment> stalePayments = paymentRepository.findStalePayments(cutoffTime);

            for (Payment payment : stalePayments) {
                payment.setStatus(Payment.PaymentStatus.CANCELLED);
                paymentRepository.save(payment);
                log.info("Cancelled stale payment: {}", payment.getTransactionId());
            }

            log.info("Cleaned up {} stale payments", stalePayments.size());
        } catch (Exception e) {
            log.error("Error in cleanupStalePayments scheduled task", e);
        }
    }

    /**
     * Send appointment reminders every day at 6 PM
     */
    @Scheduled(cron = "0 0 18 * * *")
    public void sendAppointmentReminders() {
        try {
            log.info("Running scheduled task: Send appointment reminders");

            LocalDate tomorrow = LocalDate.now().plusDays(1);
            List<ServiceRequest> tomorrowAppointments = serviceRequestRepository.findByPreferredDate(tomorrow);

            for (ServiceRequest request : tomorrowAppointments) {
                if (request.getStatus() == ServiceRequest.RequestStatus.BOOKING_CONFIRMED) {
                    notificationService.notifyAppointmentReminder(request);

                    // Send email reminder if email service is configured
                    try {
                        sendAppointmentReminderEmail(request);
                    } catch (Exception e) {
                        log.warn("Failed to send email reminder for request: {}", request.getId(), e);
                    }
                }
            }

            log.info("Sent {} appointment reminders", tomorrowAppointments.size());
        } catch (Exception e) {
            log.error("Error in sendAppointmentReminders scheduled task", e);
        }
    }

    /**
     * Generate daily summary report every day at 11 PM
     */
    @Scheduled(cron = "0 0 23 * * *")
    public void generateDailySummary() {
        try {
            log.info("Running scheduled task: Generate daily summary");

            LocalDate today = LocalDate.now();

            // Count today's activities
            long todayRequests = serviceRequestRepository.findByPreferredDate(today).size();
            long todayPayments = paymentRepository.countTodaysSuccessfulPayments();
            long pendingQuotes = serviceQuoteService.getPendingQuotes().size();
            long overdueInvoices = invoiceRepository.findOverdueInvoices(LocalDateTime.now()).size();

            log.info("Daily Summary for {}: Requests={}, Payments={}, Pending Quotes={}, Overdue Invoices={}",
                    today, todayRequests, todayPayments, pendingQuotes, overdueInvoices);

            // In a production system, you might send this summary to administrators

        } catch (Exception e) {
            log.error("Error in generateDailySummary scheduled task", e);
        }
    }

    /**
     * Backup important data every Sunday at 1 AM
     */
    @Scheduled(cron = "0 0 1 * * SUN")
    public void performWeeklyMaintenance() {
        try {
            log.info("Running scheduled task: Weekly maintenance");

            // Archive old completed service requests
            archiveOldServiceRequests();

            // Clean up old temporary files
            cleanupOldFiles();

            // Generate weekly summary report
            generateWeeklySummary();

            log.info("Weekly maintenance completed successfully");
        } catch (Exception e) {
            log.error("Error in performWeeklyMaintenance scheduled task", e);
        }
    }

    private void sendOverdueNotification(Invoice invoice) {
        String clientEmail = invoice.getServiceRequest().getClient().getEmail();
        String subject = "Overdue Invoice - " + invoice.getInvoiceNumber();
        String body = String.format(
                "Dear %s,\n\n" +
                        "This is a reminder that invoice %s for R%.2f is now overdue.\n" +
                        "Please make payment as soon as possible to avoid any service interruptions.\n\n" +
                        "Thank you,\nTimatix Auto Works",
                invoice.getServiceRequest().getClient().getFirstName(),
                invoice.getInvoiceNumber(),
                invoice.getTotalAmount()
        );

        emailService.sendEmail(clientEmail, subject, body);
    }

    private void sendAppointmentReminderEmail(ServiceRequest request) {
        String clientEmail = request.getClient().getEmail();
        String subject = "Service Appointment Reminder";
        String body = String.format(
                "Dear %s,\n\n" +
                        "This is a reminder that your %s service for %s %s is scheduled for tomorrow at %s.\n\n" +
                        "Please ensure your vehicle is ready for service.\n\n" +
                        "Thank you,\nTimatix Auto Works",
                request.getClient().getFirstName(),
                request.getService().getName(),
                request.getVehicle().getMake(),
                request.getVehicle().getModel(),
                request.getPreferredTime()
        );

        emailService.sendEmail(clientEmail, subject, body);
    }

    private void archiveOldServiceRequests() {
        // Archive service requests older than 1 year that are completed
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        log.info("Archiving old service requests older than: {}", oneYearAgo);

        // In a real implementation, you would move old records to an archive table
        // For now, just log the action
        log.info("Archive process completed (simulated)");
    }

    private void cleanupOldFiles() {
        // Clean up uploaded files older than 6 months
        log.info("Cleaning up old uploaded files");

        // In a real implementation, you would scan the upload directory
        // and remove files older than the retention period
        log.info("File cleanup completed (simulated)");
    }

    private void generateWeeklySummary() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);

        // Generate and log weekly summary
        log.info("Weekly Summary ({} to {}): System running normally", startDate, endDate);

        // In a production system, you might send this to administrators
    }
}