package com.timatix.servicebooking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceQuoteDto {

    @NotNull(message = "Request ID is required")
    private Long requestId;

    @NotNull(message = "Mechanic ID is required")
    private Long mechanicId;

    private String lineItemsJson;

    @PositiveOrZero(message = "Labour cost must be positive or zero")
    private BigDecimal labourCost;

    @PositiveOrZero(message = "Parts cost must be positive or zero")
    private BigDecimal partsCost;

    @NotNull(message = "Total amount is required")
    @PositiveOrZero(message = "Total amount must be positive or zero")
    private BigDecimal totalAmount;

    private String notes;
    private LocalDateTime validUntil;
}