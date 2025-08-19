package com.timatix.servicebooking.model;

import jakarta.persistence.*;

@Entity
public class Vehicle {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String make;
    private String model;
    private String year;
    private String licensePlate;
    private String vin;

    @ManyToOne
    private User owner;

    // Getters and Setters
}