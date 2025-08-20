package com.timatix.servicebooking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Vehicle creation/update request")
public class VehicleDto {

    @Schema(description = "Vehicle make", example = "Toyota")
    @NotBlank(message = "Vehicle make is required")
    @Size(max = 100, message = "Make cannot exceed 100 characters")
    private String make;

    @Schema(description = "Vehicle model", example = "Camry")
    @NotBlank(message = "Vehicle model is required")
    @Size(max = 100, message = "Model cannot exceed 100 characters")
    private String model;

    @Schema(description = "Vehicle year", example = "2020")
    @NotBlank(message = "Vehicle year is required")
    @Pattern(regexp = "^(19|20)\\d{2}$", message = "Year must be a valid 4-digit year")
    private String year;

    @Schema(description = "License plate number", example = "CA123GP")
    @Size(max = 20, message = "License plate cannot exceed 20 characters")
    private String licensePlate;

    @Schema(description = "Vehicle Identification Number", example = "1HGBH41JXMN109186")
    @Size(max = 100, message = "VIN cannot exceed 100 characters")
    private String vin;

    @Schema(description = "Vehicle color", example = "Silver")
    @Size(max = 50, message = "Color cannot exceed 50 characters")
    private String color;

    @Schema(description = "Photo URL")
    private String photoUrl;

    @Schema(description = "Owner ID", example = "1")
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
}
