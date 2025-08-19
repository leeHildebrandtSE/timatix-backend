package com.timatix.servicebooking.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        CLIENT, MECHANIC, ADMIN
    }

    // Getters and Setters
}