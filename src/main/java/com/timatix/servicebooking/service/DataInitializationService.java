package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.ServiceCatalog;
import com.timatix.servicebooking.model.ServiceQuote;
import com.timatix.servicebooking.model.ServiceRequest;
import com.timatix.servicebooking.model.User;
import com.timatix.servicebooking.model.Vehicle;
import com.timatix.servicebooking.repository.ServiceCatalogRepository;
import com.timatix.servicebooking.repository.ServiceQuoteRepository;
import com.timatix.servicebooking.repository.ServiceRequestRepository;
import com.timatix.servicebooking.repository.UserRepository;
import com.timatix.servicebooking.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final VehicleRepository vehicleRepository;
    private final BookingSlotService bookingSlotService;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ServiceQuoteRepository serviceQuoteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting enhanced data initialization...");

        initializeDefaultUsers();
        initializeServiceCatalog();
        initializeBookingSlots();
        initializeSampleData();

        log.info("Enhanced data initialization completed successfully!");
    }

    private void initializeDefaultUsers() {
        // Check if admin user already exists
        if (userRepository.findByEmail("admin@timatix.com").isEmpty()) {
            User admin = new User();
            admin.setFirstName("System Administrator");
            admin.setEmail("admin@timatix.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhoneNumber("+27123456789");
            admin.setAddress("Timatix Headquarters, Cape Town");
            admin.setRole(User.Role.ADMIN);

            userRepository.save(admin);
            log.info("Created default admin user: admin@timatix.com");
        }

        // Create multiple mechanics
        createMechanicIfNotExists("Mike Smith", "mike@timatix.com", "+27123456790");
        createMechanicIfNotExists("Sarah Johnson", "sarah@timatix.com", "+27123456791");
        createMechanicIfNotExists("David Wilson", "david.mechanic@timatix.com", "+27123456792");

        // Create sample clients
        createClientIfNotExists("John Doe", "john.doe@email.com", "+27123456793");
        createClientIfNotExists("Emma Brown", "emma.brown@email.com", "+27123456794");
        createClientIfNotExists("Alex Taylor", "alex.taylor@email.com", "+27123456795");
    }

    private void createMechanicIfNotExists(String name, String email, String phone) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User mechanic = new User();
            mechanic.setFirstName(name);
            mechanic.setEmail(email);
            mechanic.setPassword(passwordEncoder.encode("mechanic123"));
            mechanic.setPhoneNumber(phone);
            mechanic.setAddress("Workshop Bay, Cape Town");
            mechanic.setRole(User.Role.MECHANIC);

            userRepository.save(mechanic);
            log.info("Created mechanic user: {}", email);
        }
    }

    private void createClientIfNotExists(String name, String email, String phone) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User client = new User();
            client.setFirstName(name);
            client.setEmail(email);
            client.setPassword(passwordEncoder.encode("client123"));
            client.setPhoneNumber(phone);
            client.setAddress("Client Address, Cape Town");
            client.setRole(User.Role.CLIENT);

            userRepository.save(client);
            log.info("Created client user: {}", email);
        }
    }

    private void initializeServiceCatalog() {
        if (serviceCatalogRepository.count() == 0) {
            List<ServiceCatalog> defaultServices = Arrays.asList(
                    createService("Oil Change", "Standard oil and filter change service", new BigDecimal("450.00"), 30),
                    createService("Brake Service", "Complete brake inspection and service", new BigDecimal("1200.00"), 60),
                    createService("Tire Rotation", "Rotate tires for even wear", new BigDecimal("250.00"), 20),
                    createService("Engine Diagnostic", "Computer diagnostic scan of engine systems", new BigDecimal("350.00"), 45),
                    createService("Battery Test", "Battery load test and replacement if needed", new BigDecimal("180.00"), 15),
                    createService("Transmission Service", "Transmission fluid change and inspection", new BigDecimal("800.00"), 90),
                    createService("Air Filter Replacement", "Replace engine air filter", new BigDecimal("120.00"), 10),
                    createService("Wheel Alignment", "4-wheel alignment service", new BigDecimal("650.00"), 45),
                    createService("Coolant Flush", "Complete cooling system flush and refill", new BigDecimal("400.00"), 40),
                    createService("Spark Plug Replacement", "Replace spark plugs and ignition components", new BigDecimal("320.00"), 30),
                    createService("Full Service", "Comprehensive vehicle service and inspection", new BigDecimal("850.00"), 120),
                    createService("Clutch Repair", "Clutch inspection and replacement", new BigDecimal("2500.00"), 180)
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
        // Generate booking slots for the next 60 days (weekdays only)
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(60);

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
            bookingSlotService.generateWeekdaySlotsForDateRange(startDate, endDate, timeSlots, 3);
            log.info("Generated booking slots from {} to {}", startDate, endDate);
        } catch (Exception e) {
            log.warn("Booking slots may already exist: {}", e.getMessage());
        }
    }

    private void initializeSampleData() {
        // Create sample vehicles for clients
        List<User> clients = userRepository.findAllClients();
        if (!clients.isEmpty() && vehicleRepository.count() == 0) {
            for (User client : clients) {
                createSampleVehiclesForClient(client);
            }
        }

        // Create sample service requests
        if (serviceRequestRepository.count() == 0) {
            createSampleServiceRequests();
        }
    }

    private void createSampleVehiclesForClient(User client) {
        List<Vehicle> vehicles = Arrays.asList(
                createVehicle("Toyota", "Camry", "2020", "CA123GP", "1HGBH41JXMN109186", "Silver", client),
                createVehicle("Honda", "Civic", "2019", "CA456GP", "2HGBH41JXMN109187", "Blue", client)
        );

        vehicleRepository.saveAll(vehicles);
        log.info("Created {} vehicles for client: {}", vehicles.size(), client.getEmail());
    }

    private Vehicle createVehicle(String make, String model, String year, String licensePlate,
                                  String vin, String color, User owner) {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake(make);
        vehicle.setModel(model);
        vehicle.setYear(year);
        vehicle.setLicensePlate(licensePlate);
        vehicle.setVin(vin);
        vehicle.setColor(color);
        vehicle.setOwner(owner);
        return vehicle;
    }

    private void createSampleServiceRequests() {
        List<User> clients = userRepository.findAllClients();
        List<User> mechanics = userRepository.findAllMechanics();
        List<ServiceCatalog> services = serviceCatalogRepository.findByIsActiveTrue();

        if (!clients.isEmpty() && !services.isEmpty()) {
            User client = clients.get(0);
            List<Vehicle> vehicles = vehicleRepository.findByOwner(client);

            if (!vehicles.isEmpty()) {
                ServiceRequest request = new ServiceRequest();
                request.setClient(client);
                request.setVehicle(vehicles.get(0));
                request.setService(services.get(0)); // Oil Change
                request.setPreferredDate(LocalDate.now().plusDays(3));
                request.setPreferredTime(LocalTime.of(9, 0));
                request.setNotes("Car is making unusual noises during startup");
                request.setStatus(ServiceRequest.RequestStatus.PENDING_QUOTE);

                if (!mechanics.isEmpty()) {
                    request.setAssignedMechanic(mechanics.get(0));
                }

                serviceRequestRepository.save(request);
                log.info("Created sample service request");

                // Create a sample quote
                if (!mechanics.isEmpty()) {
                    createSampleQuote(request, mechanics.get(0));
                }
            }
        }
    }

    private void createSampleQuote(ServiceRequest request, User mechanic) {
        ServiceQuote quote = new ServiceQuote();
        quote.setRequest(request);
        quote.setMechanic(mechanic);
        quote.setLabourCost(new BigDecimal("300.00"));
        quote.setPartsCost(new BigDecimal("150.00"));
        quote.setTotalAmount(new BigDecimal("450.00"));
        quote.setNotes("Standard oil change with premium oil filter");
        quote.setApprovalStatus(ServiceQuote.ApprovalStatus.PENDING);

        serviceQuoteRepository.save(quote);

        // Update request status
        request.setStatus(ServiceRequest.RequestStatus.QUOTE_SENT);
        serviceRequestRepository.save(request);

        log.info("Created sample quote for service request");
    }
}