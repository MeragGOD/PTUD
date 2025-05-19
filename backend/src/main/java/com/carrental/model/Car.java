package com.carrental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String make;

    @NotBlank
    private String model;

    @NotNull
    private Integer year;

    @NotNull
    @Positive
    private BigDecimal pricePerDay;

    private boolean available = true;

    private String imageUrl;

    private String licensePlate;

    private String color;

    private String transmission;

    private Integer seats;

    private String fuelType;
}
