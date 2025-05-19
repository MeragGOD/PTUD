package com.carrental.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarResponse {
    private Long id;
    private String make;
    private String model;
    private Integer year;
    private BigDecimal pricePerDay;
    private boolean available;
    private String imageUrl;
    private String licensePlate;
    private String color;
    private String transmission;
    private Integer seats;
    private String fuelType;
}
