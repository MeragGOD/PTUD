package com.carrental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarRequest {

    @NotBlank(message = "Make is required")
    private String make;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Price per day is required")
    @Positive(message = "Price per day must be positive")
    private BigDecimal pricePerDay;

    private boolean available = true;

    private String licensePlate;

    private String color;

    private String transmission;

    private Integer seats;

    private String fuelType;
}
