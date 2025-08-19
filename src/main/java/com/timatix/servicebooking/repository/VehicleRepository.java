package com.timatix.servicebooking.repository;

import com.timatix.bookingservices.model.Vehicle;
import com.timatix.bookingservices.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByOwner(User owner);
}