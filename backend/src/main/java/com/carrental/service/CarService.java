package com.carrental.service;

import com.carrental.dto.request.CarRequest;
import com.carrental.dto.response.CarResponse;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.model.Car;
import com.carrental.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final FileStorageService fileStorageService;

    public List<CarResponse> getAllCars() {
        return carRepository.findAll().stream()
                .map(this::mapToCarResponse)
                .collect(Collectors.toList());
    }

    public List<CarResponse> getAvailableCars() {
        return carRepository.findByAvailableTrue().stream()
                .map(this::mapToCarResponse)
                .collect(Collectors.toList());
    }

    public CarResponse getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));
        return mapToCarResponse(car);
    }

    public List<CarResponse> searchCars(String make, String model, BigDecimal minPrice, BigDecimal maxPrice, Boolean available) {
        return carRepository.searchCars(make, model, minPrice, maxPrice, available).stream()
                .map(this::mapToCarResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CarResponse createCar(CarRequest carRequest) {
        Car car = Car.builder()
                .make(carRequest.getMake())
                .model(carRequest.getModel())
                .year(carRequest.getYear())
                .pricePerDay(carRequest.getPricePerDay())
                .available(carRequest.isAvailable())
                .licensePlate(carRequest.getLicensePlate())
                .color(carRequest.getColor())
                .transmission(carRequest.getTransmission())
                .seats(carRequest.getSeats())
                .fuelType(carRequest.getFuelType())
                .build();

        Car savedCar = carRepository.save(car);
        return mapToCarResponse(savedCar);
    }

    @Transactional
    public CarResponse updateCar(Long id, CarRequest carRequest) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));

        car.setMake(carRequest.getMake());
        car.setModel(carRequest.getModel());
        car.setYear(carRequest.getYear());
        car.setPricePerDay(carRequest.getPricePerDay());
        car.setAvailable(carRequest.isAvailable());
        car.setLicensePlate(carRequest.getLicensePlate());
        car.setColor(carRequest.getColor());
        car.setTransmission(carRequest.getTransmission());
        car.setSeats(carRequest.getSeats());
        car.setFuelType(carRequest.getFuelType());

        Car updatedCar = carRepository.save(car);
        return mapToCarResponse(updatedCar);
    }

    @Transactional
    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new ResourceNotFoundException("Car not found with id: " + id);
        }
        carRepository.deleteById(id);
    }

    @Transactional
    public CarResponse uploadCarImage(Long id, MultipartFile file) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));

        String fileName = fileStorageService.storeFile(file);
        car.setImageUrl(fileName);
        Car updatedCar = carRepository.save(car);
        return mapToCarResponse(updatedCar);
    }

    private CarResponse mapToCarResponse(Car car) {
        return CarResponse.builder()
                .id(car.getId())
                .make(car.getMake())
                .model(car.getModel())
                .year(car.getYear())
                .pricePerDay(car.getPricePerDay())
                .available(car.isAvailable())
                .imageUrl(car.getImageUrl())
                .licensePlate(car.getLicensePlate())
                .color(car.getColor())
                .transmission(car.getTransmission())
                .seats(car.getSeats())
                .fuelType(car.getFuelType())
                .build();
    }
}
