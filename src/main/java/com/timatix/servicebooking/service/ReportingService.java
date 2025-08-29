package com.timatix.servicebooking.service;

import com.timatix.servicebooking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportingService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final ServiceQuoteRepository serviceQuoteRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceCatalogRepository serviceCatalogRepository;

    public Map<String, Object> generateFinancialReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        Map<String, Object> report = new HashMap<>();

        // Revenue metrics
        BigDecimal totalRevenue = BigDecimal.valueOf(invoiceRepository.getTotalRevenueByDateRange(start, end));
        BigDecimal totalPayments = paymentRepository.getTotalRevenueByDateRange(start, end);
        Long totalInvoices = invoiceRepository.count();
        Long paidInvoices = (long) invoiceRepository.findByPaymentStatus(
                com.timatix.servicebooking.model.Invoice.PaymentStatus.PAID).size();

        report.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        report.put("totalPayments", totalPayments != null ? totalPayments : BigDecimal.ZERO);
        report.put("totalInvoices", totalInvoices);
        report.put("paidInvoices", paidInvoices);
        report.put("outstandingAmount",
                (totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                        .subtract(totalPayments != null ? totalPayments : BigDecimal.ZERO));

        // Service performance
        List<Object[]> popularServices = getPopularServices(startDate, endDate);
        report.put("popularServices", popularServices);

        // Monthly revenue trend
        List<Map<String, Object>> monthlyRevenue = getMonthlyRevenueTrend(startDate, endDate);
        report.put("monthlyRevenue", monthlyRevenue);

        // Payment method analysis
        Map<String, Object> paymentMethods = getPaymentMethodAnalysis(start, end);
        report.put("paymentMethods", paymentMethods);

        return report;
    }

    public Map<String, Object> generateOperationalReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();

        // Service request metrics
        Long totalRequests = serviceRequestRepository.count();
        Long completedRequests = (long) serviceRequestRepository.findByStatus(
                com.timatix.servicebooking.model.ServiceRequest.RequestStatus.COMPLETED).size();
        Long pendingRequests = (long) serviceRequestRepository.findByStatus(
                com.timatix.servicebooking.model.ServiceRequest.RequestStatus.PENDING_QUOTE).size();

        report.put("totalServiceRequests", totalRequests);
        report.put("completedRequests", completedRequests);
        report.put("pendingRequests", pendingRequests);
        report.put("completionRate", totalRequests > 0 ?
                (double) completedRequests / totalRequests * 100 : 0);

        // Quote metrics
        Long totalQuotes = serviceQuoteRepository.count();
        Long approvedQuotes = (long) serviceQuoteRepository.findByApprovalStatus(
                com.timatix.servicebooking.model.ServiceQuote.ApprovalStatus.ACCEPTED).size();

        report.put("totalQuotes", totalQuotes);
        report.put("approvedQuotes", approvedQuotes);
        report.put("quoteApprovalRate", totalQuotes > 0 ?
                (double) approvedQuotes / totalQuotes * 100 : 0);

        // Mechanic performance
        List<Map<String, Object>> mechanicPerformance = getMechanicPerformance();
        report.put("mechanicPerformance", mechanicPerformance);

        // Customer metrics
        Long totalClients = (long) userRepository.findAllClients().size();
        Long totalVehicles = vehicleRepository.count();
        report.put("totalClients", totalClients);
        report.put("totalVehicles", totalVehicles);

        return report;
    }

    public Map<String, Object> generateCustomerReport(Long clientId) {
        Map<String, Object> report = new HashMap<>();

        // Service history
        List<Object[]> serviceHistory = getClientServiceHistory(clientId);
        report.put("serviceHistory", serviceHistory);

        // Spending analysis
        BigDecimal totalSpent = getClientTotalSpending(clientId);
        report.put("totalSpent", totalSpent);

        // Vehicle information
        List<Object[]> vehicles = getClientVehicles(clientId);
        report.put("vehicles", vehicles);

        // Recent activity
        List<Object[]> recentActivity = getClientRecentActivity(clientId);
        report.put("recentActivity", recentActivity);

        return report;
    }

    public Map<String, Object> generateMechanicReport(Long mechanicId) {
        Map<String, Object> report = new HashMap<>();

        // Job statistics
        Long assignedJobs = (long) serviceRequestRepository.findByAssignedMechanicId(mechanicId).size();
        Long completedJobs = (long) serviceRequestRepository.findByMechanicIdAndStatus(mechanicId,
                com.timatix.servicebooking.model.ServiceRequest.RequestStatus.COMPLETED).size();

        report.put("assignedJobs", assignedJobs);
        report.put("completedJobs", completedJobs);
        report.put("completionRate", assignedJobs > 0 ?
                (double) completedJobs / assignedJobs * 100 : 0);

        // Quote statistics
        Long quotesCreated = (long) serviceQuoteRepository.findByMechanicId(mechanicId).size();
        Long quotesAccepted = (long) serviceQuoteRepository.findByMechanicIdAndStatus(mechanicId,
                com.timatix.servicebooking.model.ServiceQuote.ApprovalStatus.ACCEPTED).size();

        report.put("quotesCreated", quotesCreated);
        report.put("quotesAccepted", quotesAccepted);
        report.put("quoteAcceptanceRate", quotesCreated > 0 ?
                (double) quotesAccepted / quotesCreated * 100 : 0);

        // Revenue generated
        BigDecimal revenueGenerated = getMechanicRevenue(mechanicId);
        report.put("revenueGenerated", revenueGenerated);

        return report;
    }

    public List<Map<String, Object>> generateInventoryReport() {
        // Mock inventory report - in a real system, this would track parts usage
        List<Map<String, Object>> inventory = new ArrayList<>();

        Map<String, Object> oilFilters = new HashMap<>();
        oilFilters.put("partName", "Oil Filters");
        oilFilters.put("currentStock", 45);
        oilFilters.put("reorderLevel", 20);
        oilFilters.put("lastUsed", LocalDate.now().minusDays(2));
        oilFilters.put("averageUsage", 8);

        Map<String, Object> brakePads = new HashMap<>();
        brakePads.put("partName", "Brake Pads");
        brakePads.put("currentStock", 12);
        brakePads.put("reorderLevel", 15);
        brakePads.put("lastUsed", LocalDate.now().minusDays(1));
        brakePads.put("averageUsage", 3);

        inventory.add(oilFilters);
        inventory.add(brakePads);

        return inventory;
    }

    private List<Object[]> getPopularServices(LocalDate startDate, LocalDate endDate) {
        // This would be implemented with a proper query
        // For now, returning mock data
        List<Object[]> popularServices = new ArrayList<>();
        popularServices.add(new Object[]{"Oil Change", 25L, new BigDecimal("11250.00")});
        popularServices.add(new Object[]{"Brake Service", 8L, new BigDecimal("9600.00")});
        popularServices.add(new Object[]{"Engine Diagnostic", 12L, new BigDecimal("4200.00")});
        return popularServices;
    }

    private List<Map<String, Object>> getMonthlyRevenueTrend(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> monthlyData = new ArrayList<>();

        YearMonth start = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);

        YearMonth current = start;
        while (!current.isAfter(end)) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", current.toString());
            monthData.put("revenue", new BigDecimal("15000.00")); // Mock data
            monthData.put("invoices", 20L);
            monthlyData.add(monthData);
            current = current.plusMonths(1);
        }

        return monthlyData;
    }

    private Map<String, Object> getPaymentMethodAnalysis(LocalDateTime start, LocalDateTime end) {
        Map<String, Object> analysis = new HashMap<>();

        // Mock payment method data
        Map<String, Object> creditCard = new HashMap<>();
        creditCard.put("count", 45L);
        creditCard.put("amount", new BigDecimal("22500.00"));

        Map<String, Object> bankTransfer = new HashMap<>();
        bankTransfer.put("count", 25L);
        bankTransfer.put("amount", new BigDecimal("18750.00"));

        Map<String, Object> cash = new HashMap<>();
        cash.put("count", 15L);
        cash.put("amount", new BigDecimal("6750.00"));

        analysis.put("Credit Card", creditCard);
        analysis.put("Bank Transfer", bankTransfer);
        analysis.put("Cash", cash);

        return analysis;
    }

    private List<Map<String, Object>> getMechanicPerformance() {
        List<Map<String, Object>> performance = new ArrayList<>();

        List<Object[]> mechanics = userRepository.findAllMechanics().stream()
                .map(user -> new Object[]{user.getId(), user.getFirstName()})
                .collect(Collectors.toList());

        for (Object[] mechanic : mechanics) {
            Map<String, Object> mechanicData = new HashMap<>();
            mechanicData.put("mechanicId", mechanic[0]);
            mechanicData.put("mechanicName", mechanic[1]);
            mechanicData.put("assignedJobs", 15L);
            mechanicData.put("completedJobs", 12L);
            mechanicData.put("averageCompletionTime", "2.5 days");
            mechanicData.put("customerRating", 4.5);
            performance.add(mechanicData);
        }

        return performance;
    }

    private List<Object[]> getClientServiceHistory(Long clientId) {
        // This would query service_requests joined with services and vehicles
        List<Object[]> history = new ArrayList<>();
        history.add(new Object[]{
                LocalDate.now().minusDays(30), "Oil Change", "Toyota Camry", "COMPLETED", new BigDecimal("450.00")
        });
        history.add(new Object[]{
                LocalDate.now().minusDays(60), "Brake Service", "Toyota Camry", "COMPLETED", new BigDecimal("1200.00")
        });
        return history;
    }

    private BigDecimal getClientTotalSpending(Long clientId) {
        // This would sum all paid invoices for the client
        return new BigDecimal("1650.00");
    }

    private List<Object[]> getClientVehicles(Long clientId) {
        return vehicleRepository.findByOwnerId(clientId).stream()
                .map(vehicle -> new Object[]{
                        vehicle.getId(),
                        vehicle.getMake() + " " + vehicle.getModel(),
                        vehicle.getYear(),
                        vehicle.getLicensePlate()
                })
                .collect(Collectors.toList());
    }

    private List<Object[]> getClientRecentActivity(Long clientId) {
        List<Object[]> activity = new ArrayList<>();
        activity.add(new Object[]{
                LocalDateTime.now().minusDays(2), "Quote Approved", "Oil Change service"
        });
        activity.add(new Object[]{
                LocalDateTime.now().minusDays(5), "Service Request Created", "Brake inspection"
        });
        return activity;
    }

    private BigDecimal getMechanicRevenue(Long mechanicId) {
        // This would sum revenue from completed jobs assigned to the mechanic
        return new BigDecimal("8500.00");
    }

    public Map<String, Object> generateDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Today's metrics
        LocalDate today = LocalDate.now();
        summary.put("todayRequests", 3L);
        summary.put("todayRevenue", new BigDecimal("1350.00"));
        // Fixed: Use the correct method name
        summary.put("pendingQuotes", serviceQuoteRepository.findPendingQuotes().size());
        summary.put("overdueInvoices", invoiceRepository.findOverdueInvoices(LocalDateTime.now()).size());

        // This week's trends
        summary.put("weeklyGrowth", 12.5);
        summary.put("customerSatisfaction", 4.7);
        summary.put("averageServiceTime", "2.3 days");

        return summary;
    }
}