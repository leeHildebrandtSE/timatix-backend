package com.timatix.servicebooking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/api-docs")
@CrossOrigin(origins = "*")
public class ApiDocsController {

    @GetMapping("/endpoints")
    public ResponseEntity<Map<String, Object>> getApiEndpoints() {
        Map<String, Object> apiDocs = new HashMap<>();

        // User endpoints
        Map<String, Object> userEndpoints = new HashMap<>();
        userEndpoints.put("POST /users/register", "Register a new user");
        userEndpoints.put("POST /users/login", "User login");
        userEndpoints.put("GET /users", "Get all users");
        userEndpoints.put("GET /users/{id}", "Get user by ID");
        userEndpoints.put("GET /users/mechanics", "Get all mechanics");
        userEndpoints.put("GET /users/clients", "Get all clients");
        userEndpoints.put("PUT /users/{id}", "Update user");
        userEndpoints.put("DELETE /users/{id}", "Delete user");

        // Vehicle endpoints
        Map<String, Object> vehicleEndpoints = new HashMap<>();
        vehicleEndpoints.put("GET /vehicles", "Get all vehicles");
        vehicleEndpoints.put("GET /vehicles/{id}", "Get vehicle by ID");
        vehicleEndpoints.put("GET /vehicles/owner/{ownerId}", "Get vehicles by owner");
        vehicleEndpoints.put("POST /vehicles", "Create vehicle");
        vehicleEndpoints.put("PUT /vehicles/{id}", "Update vehicle");
        vehicleEndpoints.put("DELETE /vehicles/{id}", "Delete vehicle");

        // Service endpoints
        Map<String, Object> serviceEndpoints = new HashMap<>();
        serviceEndpoints.put("GET /services", "Get all services");
        serviceEndpoints.put("GET /services/active", "Get active services");
        serviceEndpoints.put("GET /services/{id}", "Get service by ID");
        serviceEndpoints.put("POST /services", "Create service");
        serviceEndpoints.put("PUT /services/{id}", "Update service");
        serviceEndpoints.put("DELETE /services/{id}", "Delete service");

        // Booking endpoints
        Map<String, Object> bookingEndpoints = new HashMap<>();
        bookingEndpoints.put("GET /booking-slots", "Get all booking slots");
        bookingEndpoints.put("GET /booking-slots/available", "Get available slots");
        bookingEndpoints.put("GET /booking-slots/date/{date}", "Get slots by date");
        bookingEndpoints.put("POST /booking-slots", "Create booking slot");
        bookingEndpoints.put("PUT /booking-slots/{id}", "Update booking slot");
        bookingEndpoints.put("DELETE /booking-slots/{id}", "Delete booking slot");

        // Service request endpoints
        Map<String, Object> requestEndpoints = new HashMap<>();
        requestEndpoints.put("GET /service-requests", "Get all service requests");
        requestEndpoints.put("GET /service-requests/{id}", "Get service request by ID");
        requestEndpoints.put("GET /service-requests/client/{clientId}", "Get requests by client");
        requestEndpoints.put("POST /service-requests", "Create service request");
        requestEndpoints.put("PUT /service-requests/{id}/assign-mechanic/{mechanicId}", "Assign mechanic");
        requestEndpoints.put("PUT /service-requests/{id}/status", "Update status");

        // Quote endpoints
        Map<String, Object> quoteEndpoints = new HashMap<>();
        quoteEndpoints.put("GET /service-quotes", "Get all quotes");
        quoteEndpoints.put("GET /service-quotes/{id}", "Get quote by ID");
        quoteEndpoints.put("GET /service-quotes/pending", "Get pending quotes");
        quoteEndpoints.put("POST /service-quotes", "Create quote");
        quoteEndpoints.put("PUT /service-quotes/{id}/approve", "Approve quote");
        quoteEndpoints.put("PUT /service-quotes/{id}/decline", "Decline quote");

        // Payment endpoints
        Map<String, Object> paymentEndpoints = new HashMap<>();
        paymentEndpoints.put("POST /payments/process", "Process payment");
        paymentEndpoints.put("POST /payments/refund/{paymentId}", "Process refund");
        paymentEndpoints.put("GET /payments/invoice/{invoiceId}", "Get payments by invoice");
        paymentEndpoints.put("GET /payments/pending", "Get pending payments");
        paymentEndpoints.put("GET /payments/payment-url/{invoiceId}", "Generate payment URL");

        // Report endpoints
        Map<String, Object> reportEndpoints = new HashMap<>();
        reportEndpoints.put("GET /reports/financial", "Financial report");
        reportEndpoints.put("GET /reports/operational", "Operational report");
        reportEndpoints.put("GET /reports/customer/{clientId}", "Customer report");
        reportEndpoints.put("GET /reports/mechanic/{mechanicId}", "Mechanic report");
        reportEndpoints.put("GET /reports/dashboard", "Dashboard summary");

        // Health endpoints
        Map<String, Object> healthEndpoints = new HashMap<>();
        healthEndpoints.put("GET /health", "Health check");
        healthEndpoints.put("GET /health/info", "Application info");
        healthEndpoints.put("GET /health/ready", "Readiness check");
        healthEndpoints.put("GET /health/live", "Liveness check");

        apiDocs.put("users", userEndpoints);
        apiDocs.put("vehicles", vehicleEndpoints);
        apiDocs.put("services", serviceEndpoints);
        apiDocs.put("bookings", bookingEndpoints);
        apiDocs.put("serviceRequests", requestEndpoints);
        apiDocs.put("quotes", quoteEndpoints);
        apiDocs.put("payments", paymentEndpoints);
        apiDocs.put("reports", reportEndpoints);
        apiDocs.put("health", healthEndpoints);

        return ResponseEntity.ok(apiDocs);
    }

    @GetMapping("/workflow")
    public ResponseEntity<Map<String, Object>> getWorkflow() {
        Map<String, Object> workflow = new HashMap<>();

        List<String> steps = Arrays.asList(
                "1. Client registers and adds vehicle(s)",
                "2. Client creates service request",
                "3. Admin assigns mechanic to request",
                "4. Mechanic creates quote",
                "5. Client approves/declines quote",
                "6. If approved, booking is confirmed",
                "7. Service is performed with progress updates",
                "8. Invoice is generated",
                "9. Client makes payment",
                "10. Service is completed"
        );

        Map<String, String> statuses = new HashMap<>();
        statuses.put("PENDING_QUOTE", "Waiting for mechanic to create quote");
        statuses.put("QUOTE_SENT", "Quote sent to client for approval");
        statuses.put("QUOTE_APPROVED", "Client approved quote");
        statuses.put("QUOTE_DECLINED", "Client declined quote");
        statuses.put("BOOKING_CONFIRMED", "Service booking confirmed");
        statuses.put("IN_PROGRESS", "Service is being performed");
        statuses.put("COMPLETED", "Service completed");
        statuses.put("CANCELLED", "Service cancelled");

        workflow.put("workflowSteps", steps);
        workflow.put("requestStatuses", statuses);

        return ResponseEntity.ok(workflow);
    }

    @GetMapping("/sample-data")
    public ResponseEntity<Map<String, Object>> getSampleData() {
        Map<String, Object> samples = new HashMap<>();

        // Sample user registration
        Map<String, Object> userRegistration = new HashMap<>();
        userRegistration.put("name", "John Doe");
        userRegistration.put("email", "john@email.com");
        userRegistration.put("password", "password123");
        userRegistration.put("phone", "+27123456789");
        userRegistration.put("address", "123 Main St, Cape Town");
        userRegistration.put("role", "CLIENT");

        // Sample vehicle creation
        Map<String, Object> vehicleCreation = new HashMap<>();
        vehicleCreation.put("make", "Toyota");
        vehicleCreation.put("model", "Camry");
        vehicleCreation.put("year", "2020");
        vehicleCreation.put("licensePlate", "CA123GP");
        vehicleCreation.put("vin", "1HGBH41JXMN109186");
        vehicleCreation.put("color", "Silver");

        // Sample service request
        Map<String, Object> serviceRequest = new HashMap<>();
        serviceRequest.put("clientId", 1);
        serviceRequest.put("vehicleId", 1);
        serviceRequest.put("serviceId", 1);
        serviceRequest.put("preferredDate", "2025-08-25");
        serviceRequest.put("preferredTime", "09:00:00");
        serviceRequest.put("notes", "Car needs oil change");

        // Sample quote creation
        Map<String, Object> quoteCreation = new HashMap<>();
        quoteCreation.put("labourCost", 300.00);
        quoteCreation.put("partsCost", 150.00);
        quoteCreation.put("totalAmount", 450.00);
        quoteCreation.put("notes", "Standard oil change service");

        // Sample payment processing
        Map<String, Object> paymentProcessing = new HashMap<>();
        paymentProcessing.put("invoiceId", 1);
        paymentProcessing.put("amount", 517.50);
        paymentProcessing.put("paymentMethod", "Credit Card");

        samples.put("userRegistration", userRegistration);
        samples.put("vehicleCreation", vehicleCreation);
        samples.put("serviceRequest", serviceRequest);
        samples.put("quoteCreation", quoteCreation);
        samples.put("paymentProcessing", paymentProcessing);

        return ResponseEntity.ok(samples);
    }
}