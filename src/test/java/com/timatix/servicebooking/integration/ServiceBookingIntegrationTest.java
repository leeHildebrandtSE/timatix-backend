package com.timatix.servicebooking.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timatix.servicebooking.model.User;
import com.timatix.servicebooking.model.Vehicle;
import com.timatix.servicebooking.repository.UserRepository;
import com.timatix.servicebooking.repository.VehicleRepository;
import com.timatix.servicebooking.repository.ServiceCatalogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebMvc
@Transactional
class ServiceBookingIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ServiceCatalogRepository serviceCatalogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private User testClient;
    private Vehicle testVehicle;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Create test client
        testClient = new User();
        testClient.setName("Test Client");
        testClient.setEmail("testclient@email.com");
        testClient.setPassword(passwordEncoder.encode("password123"));
        testClient.setRole(User.Role.CLIENT);
        testClient = userRepository.save(testClient);

        // Create test vehicle
        testVehicle = new Vehicle();
        testVehicle.setMake("Test");
        testVehicle.setModel("Vehicle");
        testVehicle.setYear("2020");
        testVehicle.setLicensePlate("TEST123");
        testVehicle.setOwner(testClient);
        testVehicle = vehicleRepository.save(testVehicle);

        // Get auth token
        authToken = authenticateUser("testclient@email.com", "password123");
    }

    @Test
    void completeServiceBookingWorkflow() throws Exception {
        // 1. Create service request
        Map<String, Object> serviceRequestData = Map.of(
                "clientId", testClient.getId(),
                "vehicleId", testVehicle.getId(),
                "serviceId", 1L, // Assuming oil change service exists
                "preferredDate", LocalDate.now().plusDays(3).toString(),
                "preferredTime", "09:00:00",
                "notes", "Integration test service request"
        );

        MvcResult requestResult = mockMvc.perform(post("/service-requests")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serviceRequestData)))
                .andExpect(status().isCreated())
                .andReturn();

        String requestResponse = requestResult.getResponse().getContentAsString();
        Map<String, Object> createdRequest = objectMapper.readValue(requestResponse, Map.class);
        Long requestId = Long.valueOf(createdRequest.get("id").toString());

        // 2. Assign mechanic (as admin)
        mockMvc.perform(put("/service-requests/" + requestId + "/assign-mechanic/2")
                        .header("Authorization", "Bearer " + getAdminToken()))
                .andExpect(status().isOk());

        // 3. Create quote (as mechanic)
        Map<String, Object> quoteData = Map.of(
                "labourCost", 300.00,
                "partsCost", 150.00,
                "totalAmount", 450.00,
                "notes", "Integration test quote"
        );

        mockMvc.perform(post("/service-quotes/request/" + requestId + "/mechanic/2")
                        .header("Authorization", "Bearer " + getMechanicToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quoteData)))
                .andExpect(status().isCreated());

        // 4. Get quote and approve it
        MvcResult quoteResult = mockMvc.perform(get("/service-quotes/request/" + requestId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();

        String quoteResponse = quoteResult.getResponse().getContentAsString();
        Map<String, Object> quote = objectMapper.readValue(quoteResponse, Map.class);
        Long quoteId = Long.valueOf(quote.get("id").toString());

        mockMvc.perform(put("/service-quotes/" + quoteId + "/approve")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        // 5. Verify request status changed
        mockMvc.perform(get("/service-requests/" + requestId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("QUOTE_APPROVED"));
    }

    @Test
    void testUserRegistrationAndVehicleCreation() throws Exception {
        // Test user registration
        Map<String, Object> userData = Map.of(
                "name", "New Test User",
                "email", "newuser@email.com",
                "password", "password123",
                "role", "CLIENT"
        );

        MvcResult userResult = mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("newuser@email.com"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String userResponse = userResult.getResponse().getContentAsString();
        Map<String, Object> registrationResult = objectMapper.readValue(userResponse, Map.class);
        Map<String, Object> user = (Map<String, Object>) registrationResult.get("user");
        String token = (String) registrationResult.get("token");
        Long userId = Long.valueOf(user.get("id").toString());

        // Test vehicle creation for new user
        Map<String, Object> vehicleData = Map.of(
                "make", "Honda",
                "model", "Civic",
                "year", "2021",
                "licensePlate", "NEW123",
                "color", "Blue"
        );

        mockMvc.perform(post("/vehicles/owner/" + userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.make").value("Honda"))
                .andExpect(jsonPath("$.model").value("Civic"));
    }

    @Test
    void testServiceCatalogOperations() throws Exception {
        // Get all services
        mockMvc.perform(get("/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Get active services
        mockMvc.perform(get("/services/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Create new service (as admin)
        Map<String, Object> serviceData = Map.of(
                "name", "Test Service",
                "description", "Integration test service",
                "basePrice", 199.99,
                "estimatedDurationMinutes", 45
        );

        mockMvc.perform(post("/services")
                        .header("Authorization", "Bearer " + getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serviceData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Service"));
    }

    @Test
    void testBookingSlotOperations() throws Exception {
        // Get available slots
        mockMvc.perform(get("/booking-slots/available"))
                .andExpect(status().isOk());

        // Get slots for specific date
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        mockMvc.perform(get("/booking-slots/available/date/" + tomorrow))
                .andExpect(status().isOk());

        // Create new booking slot (as admin)
        Map<String, Object> slotData = Map.of(
                "date", tomorrow.toString(),
                "timeSlot", "16:00:00",
                "maxBookings", 2
        );

        mockMvc.perform(post("/booking-slots")
                        .header("Authorization", "Bearer " + getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotData)))
                .andExpect(status().isCreated());
    }

    @Test
    void testHealthEndpoints() throws Exception {
        // Test main health endpoint
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));

        // Test info endpoint
        mockMvc.perform(get("/health/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application").value("Timatix Booking Services"));

        // Test readiness endpoint
        mockMvc.perform(get("/health/ready"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"));

        // Test liveness endpoint
        mockMvc.perform(get("/health/live"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ALIVE"));
    }

    private String authenticateUser(String email, String password) throws Exception {
        Map<String, String> loginData = Map.of(
                "email", email,
                "password", password
        );

        MvcResult result = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginData)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Map<String, Object> loginResult = objectMapper.readValue(response, Map.class);
        return (String) loginResult.get("token");
    }

    private String getAdminToken() throws Exception {
        return authenticateUser("admin@timatix.com", "admin123");
    }

    private String getMechanicToken() throws Exception {
        return authenticateUser("mike@timatix.com", "mechanic123");
    }
}