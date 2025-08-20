package com.timatix.servicebooking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Service catalog creation/update request")
public class ServiceCatalogDto {

    @Schema(description = "Service name", example = "Oil Change")
    @NotBlank(message = "Service name is required")
    @Size(max = 255, message = "Service name cannot exceed 255 characters")
    private String name;

    @Schema(description = "Service description", example = "Standard oil and filter change")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Schema(description = "Base price in ZAR", example = "450.00")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    private BigDecimal basePrice;

    @Schema(description = "Estimated duration in minutes", example = "30")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 480, message = "Duration cannot exceed 8 hours")
    private Integer estimatedDurationMinutes;

    @Schema(description = "Whether the service is active", example = "true")
    private Boolean isActive = true;
}