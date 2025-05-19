package com.carrental.controller;

import com.carrental.dto.request.CarRequest;
import com.carrental.dto.response.CarResponse;
import com.carrental.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
@Tag(name = "Cars", description = "Car management API")
public class CarController {

    private final CarService carService;

    @GetMapping
    @Operation(summary = "Get all cars")
    public ResponseEntity<List<CarResponse>> getAllCars() {
        List<CarResponse> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/available")
    @Operation(summary = "Get all available cars")
    public ResponseEntity<List<CarResponse>> getAvailableCars() {
        List<CarResponse> cars = carService.getAvailableCars();
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get car by ID")
    public ResponseEntity<CarResponse> getCarById(@PathVariable Long id) {
        CarResponse car = carService.getCarById(id);
        return ResponseEntity.ok(car);
    }

    @GetMapping("/search")
    @Operation(summary = "Search cars by criteria")
    public ResponseEntity<List<CarResponse>> searchCars(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean available) {
        List<CarResponse> cars = carService.searchCars(make, model, minPrice, maxPrice, available);
        return ResponseEntity.ok(cars);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new car (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CarResponse> createCar(@Valid @RequestBody CarRequest carRequest) {
        CarResponse car = carService.createCar(carRequest);
        return ResponseEntity.ok(car);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update car (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CarResponse> updateCar(@PathVariable Long id, @Valid @RequestBody CarRequest carRequest) {
        CarResponse updatedCar = carService.updateCar(id, carRequest);
        return ResponseEntity.ok(updatedCar);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete car (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Upload car image (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CarResponse> uploadCarImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        CarResponse car = carService.uploadCarImage(id, file);
        return ResponseEntity.ok(car);
    }
}
