package com.timatix.servicebooking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@timatix.com}")
    private String fromEmail;

    @Value("${app.business.name:Timatix Auto Works}")
    private String businessName;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }

    public void sendQuoteNotification(String clientEmail, String clientName, String vehicleInfo, String quoteAmount) {
        String subject = businessName + " - Service Quote Ready";
        String body = String.format(
                "Dear %s,\n\n" +
                        "Your service quote for %s is ready for review.\n\n" +
                        "Quote Amount: R%s\n\n" +
                        "Please log into the Timatix app to review and approve your quote.\n\n" +
                        "Best regards,\n" +
                        "%s Team",
                clientName, vehicleInfo, quoteAmount, businessName
        );

        sendEmail(clientEmail, subject, body);
    }

    public void sendServiceCompletionNotification(String clientEmail, String clientName, String vehicleInfo) {
        String subject = businessName + " - Service Completed";
        String body = String.format(
                "Dear %s,\n\n" +
                        "Great news! The service for your %s has been completed.\n\n" +
                        "Your vehicle is ready for collection. Please visit our workshop at your convenience.\n\n" +
                        "Thank you for choosing %s!\n\n" +
                        "Best regards,\n" +
                        "%s Team",
                clientName, vehicleInfo, businessName, businessName
        );

        sendEmail(clientEmail, subject, body);
    }
}