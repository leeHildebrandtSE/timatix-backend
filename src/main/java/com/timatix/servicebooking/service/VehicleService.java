package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.Vehicle;
import com.timatix.servicebooking.model.User;
import com.timatix.servicebooking.repository.VehicleRepository;
import com.timatix.servicebooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public List<Vehicle> getVehiclesByOwner(Long ownerId) {
        return vehicleRepository.findByOwnerId(ownerId);
    }

    public List<Vehicle> getVehiclesByOwner(User owner) {
        return vehicleRepository.findByOwner(owner);
    }

    public Optional<Vehicle> getVehicleByLicensePlate(String licensePlate) {
        return vehicleRepository.findByLicensePlate(licensePlate);
    }

    public List<Vehicle> searchVehicles(Long ownerId, String keyword) {
        return vehicleRepository.searchByOwnerIdAndKeyword(ownerId, keyword);
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        validateVehicleForCreation(vehicle);
        log.info("Creating new vehicle for owner ID: {}", vehicle.getOwner().getId());
        return vehicleRepository.save(vehicle);
    }

    public Vehicle createVehicleForOwner(Long ownerId, Vehicle vehicle) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + ownerId));

        vehicle.setOwner(owner);
        return createVehicle(vehicle);
    }

    public Vehicle updateVehicle(Long id, Vehicle vehicleDetails) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));

        vehicle.setMake(vehicleDetails.getMake());
        vehicle.setModel(vehicleDetails.getModel());
        vehicle.setYear(vehicleDetails.getYear());
        vehicle.setLicensePlate(vehicleDetails.getLicensePlate());
        vehicle.setVin(vehicleDetails.getVin());
        vehicle.setColor(vehicleDetails.getColor());
        vehicle.setPhotoUrl(vehicleDetails.getPhotoUrl());

        log.info("Updating vehicle with id: {}", id);
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new RuntimeException("Vehicle not found with id: " + id);
        }
        log.info("Deleting vehicle with id: {}", id);
        vehicleRepository.deleteById(id);
    }

    public boolean existsByLicensePlate(String licensePlate) {
        return vehicleRepository.existsByLicensePlate(licensePlate);
    }

    public boolean existsByVin(String vin) {
        return vehicleRepository.existsByVin(vin);
    }

    private void validateVehicleForCreation(Vehicle vehicle) {
        if (vehicle.getMake() == null || vehicle.getMake().trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle make is required");
        }

        if (vehicle.getModel() == null || vehicle.getModel().trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle model is required");
        }

        if (vehicle.getYear() == null || vehicle.getYear().trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle year is required");
        }

        if (vehicle.getOwner() == null) {
            throw new IllegalArgumentException("Vehicle owner is required");
        }

        // Check if license plate already exists (if provided)
        if (vehicle.getLicensePlate() != null && !vehicle.getLicensePlate().trim().isEmpty()) {
            if (vehicleRepository.existsByLicensePlate(vehicle.getLicensePlate())) {
                throw new IllegalArgumentException("Vehicle with license plate " + vehicle.getLicensePlate() + " already exists");
            }
        }

        // Check if VIN already exists (if provided)
        if (vehicle.getVin() != null && !vehicle.getVin().trim().isEmpty()) {
            if (vehicleRepository.existsByVin(vehicle.getVin())) {
                throw new IllegalArgumentException("Vehicle with VIN " + vehicle.getVin() + " already exists");
            }
        }
    }
}