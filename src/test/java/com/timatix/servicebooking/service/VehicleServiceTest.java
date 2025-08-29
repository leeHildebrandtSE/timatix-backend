// Additional test class for service layer
package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.User;
import com.timatix.servicebooking.model.Vehicle;
import com.timatix.servicebooking.repository.UserRepository;
import com.timatix.servicebooking.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class VehicleServiceTest {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create a test user
        testUser = new User();
        testUser.setFirstName("Test Owner");
        testUser.setEmail("owner@test.com");
        testUser.setPassword("password123");
        testUser.setRole(User.Role.CLIENT);
        testUser = userService.createUser(testUser);
    }

    @Test
    void shouldCreateVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setYear("2020");
        vehicle.setLicensePlate("TEST123");
        vehicle.setOwner(testUser);

        Vehicle savedVehicle = vehicleService.createVehicle(vehicle);

        assertThat(savedVehicle.getId()).isNotNull();
        assertThat(savedVehicle.getMake()).isEqualTo("Toyota");
        assertThat(savedVehicle.getModel()).isEqualTo("Camry");
        assertThat(savedVehicle.getOwner().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void shouldNotCreateVehicleWithDuplicateLicensePlate() {
        // Create first vehicle
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setMake("Toyota");
        vehicle1.setModel("Camry");
        vehicle1.setYear("2020");
        vehicle1.setLicensePlate("DUPLICATE");
        vehicle1.setOwner(testUser);
        vehicleService.createVehicle(vehicle1);

        // Try to create second vehicle with same license plate
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setMake("Honda");
        vehicle2.setModel("Civic");
        vehicle2.setYear("2021");
        vehicle2.setLicensePlate("DUPLICATE");
        vehicle2.setOwner(testUser);

        assertThatThrownBy(() -> vehicleService.createVehicle(vehicle2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void shouldGetVehiclesByOwner() {
        // Create multiple vehicles for the user
        Vehicle vehicle1 = createTestVehicle("Toyota", "Camry", "CAR001");
        Vehicle vehicle2 = createTestVehicle("Honda", "Civic", "CAR002");

        var vehicles = vehicleService.getVehiclesByOwner(testUser.getId());

        assertThat(vehicles).hasSize(2);
        assertThat(vehicles).extracting(Vehicle::getLicensePlate)
                .containsExactlyInAnyOrder("CAR001", "CAR002");
    }

    private Vehicle createTestVehicle(String make, String model, String licensePlate) {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake(make);
        vehicle.setModel(model);
        vehicle.setYear("2020");
        vehicle.setLicensePlate(licensePlate);
        vehicle.setOwner(testUser);
        return vehicleService.createVehicle(vehicle);
    }
}