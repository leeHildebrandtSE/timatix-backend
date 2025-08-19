package com.timatix.servicebooking.repository;

import com.timatix.servicebooking.model.Vehicle;
import com.timatix.servicebooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByOwner(User owner);

    List<Vehicle> findByOwnerId(Long ownerId);

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    Optional<Vehicle> findByVin(String vin);

    @Query("SELECT v FROM Vehicle v WHERE v.owner.id = :ownerId AND v.make ILIKE %:make%")
    List<Vehicle> findByOwnerIdAndMakeContainingIgnoreCase(@Param("ownerId") Long ownerId, @Param("make") String make);

    @Query("SELECT v FROM Vehicle v WHERE v.owner.id = :ownerId AND (v.make ILIKE %:search% OR v.model ILIKE %:search% OR v.licensePlate ILIKE %:search%)")
    List<Vehicle> searchByOwnerIdAndKeyword(@Param("ownerId") Long ownerId, @Param("search") String search);

    boolean existsByLicensePlate(String licensePlate);

    boolean existsByVin(String vin);
}