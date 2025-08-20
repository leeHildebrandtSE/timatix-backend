package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.*;
import com.timatix.servicebooking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataInitializationService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ServiceCatalogRepository serviceCatalogRepository;
    private final BookingSlotService bookingSlotService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");

        initializeDefaultUsers();
        initializeServiceCatalog();
        initializeBookingSlots();

        log.info("Data initialization completed successfully!");
    }

    private void initializeDefaultUsers() {
        // Check if admin user already exists
        if (userRepository.findByEmail("admin@timatix.com").isEmpty()) {
            User admin = new User();
            admin.setName("System Administrator");
            admin.setEmail("admin@timatix.com");
            admin.setPassword("admin123"); // In production, this should be hashed
            admin.setPhone("+27123456789");
            admin.setAddress("Timatix Headquarters, Cape Town");
            admin.setRole(User.Role.ADMIN);

            userRepository.save(admin);
            log.info("Created default admin user: admin@timatix.com");
        }

        // Check if default mechanic exists
        if (userRepository.findByEmail("mechanic@timatix.com").isEmpty()) {
            User mechanic = new User();
            mechanic.setName("Default Mechanic");
            mechanic.setEmail("mechanic@timatix.com");
            mechanic.setPassword("mechanic123");
            mechanic.setPhone("+27123456790");
            mechanic.setAddress("Workshop Bay 1, Cape Town");
            mechanic.setRole(User.Role.MECHANIC);

            userRepository.save(mechanic);
            log.info("Created default mechanic user: mechanic@timatix.com");
        }
    }

    private void initializeServiceCatalog() {
        if (serviceCatalogRepository.count() == 0) {
            List<ServiceCatalog> defaultServices = Arrays.asList(
                    createService("Oil Change", "Standard oil and filter change service",
                            new BigDecimal("450.00"), 30),
                    createService("Brake Service", "Complete brake inspection and service",
                            new BigDecimal("1200.00"), 60),
                    createService("Tire Rotation", "Rotate tires for even wear",
                            new BigDecimal("250.00"), 20),
                    createService("Engine Diagnostic", "Computer diagnostic scan of engine systems",
                            new BigDecimal("350.00"), 45),
                    createService("Battery Test", "Battery load test and replacement if needed",
                            new BigDecimal("180.00"), 15),
                    createService("Transmission Service", "Transmission fluid change and inspection",
                            new BigDecimal("800.00"), 90),
                    createService("Air Filter Replacement", "Replace engine air filter",
                            new BigDecimal("120.00"), 10),
                    createService("Wheel Alignment", "4-wheel alignment service",
                            new BigDecimal("650.00"), 45),
                    createService("Coolant Flush", "Complete cooling system flush and refill",
                            new BigDecimal("400.00"), 40),
                    createService("Spark Plug Replacement", "Replace spark plugs and ignition components",
                            new BigDecimal("320.00"), 30)
            );

            serviceCatalogRepository.saveAll(defaultServices);
            log.info("Created {} default services", defaultServices.size());
        }
    }

    private ServiceCatalog createService(String name, String description, BigDecimal price, Integer duration) {
        ServiceCatalog service = new ServiceCatalog();
        service.setName(name);
        service.setDescription(description);
        service.setBasePrice(price);
        service.setEstimatedDurationMinutes(duration);
        service.setIsActive(true);
        return service;
    }

    private void initializeBookingSlots() {
        // Generate booking slots for the next 30 days (weekdays only)
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(30);

        List<LocalTime> timeSlots = Arrays.asList(
                LocalTime.of(8, 0),   // 08:00
                LocalTime.of(9, 0),   // 09:00
                LocalTime.of(10, 0),  // 10:00
                LocalTime.of(11, 0),  // 11:00
                LocalTime.of(13, 0),  // 13:00 (after lunch)
                LocalTime.of(14, 0),  // 14:00
                LocalTime.of(15, 0),  // 15:00
                LocalTime.of(16, 0)   // 16:00
        );

        try {
            bookingSlotService.generateWeekdaySlotsForDateRange(startDate, endDate, timeSlots, 2);
            log.info("Generated booking slots from {} to {}", startDate, endDate);
        } catch (Exception e) {
            log.warn("Booking slots may already exist: {}", e.getMessage());
        }
    }
}