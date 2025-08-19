package com.timatix.servicebooking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestDto {

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Preferred date is required")
    @Future(message = "Preferred date must be in the future")
    private LocalDate preferredDate;

    private LocalTime preferredTime;
    private String notes;
    private String photoUrl;
}