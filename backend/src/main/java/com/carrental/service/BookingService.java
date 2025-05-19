package com.carrental.service;

import com.carrental.dto.request.BookingRequest;
import com.carrental.dto.response.BookingResponse;
import com.carrental.dto.response.CarResponse;
import com.carrental.dto.response.UserResponse;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.model.*;
import com.carrental.repository.BookingRepository;
import com.carrental.repository.CarRepository;
import com.carrental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final EmailService emailService;

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return mapToBookingResponse(booking);
    }

    public List<BookingResponse> getBookingsByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return bookingRepository.findByUser(user).stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsByCar(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + carId));
        return bookingRepository.findByCar(car).stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse createBooking(String userEmail, BookingRequest bookingRequest) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Car car = carRepository.findById(bookingRequest.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + bookingRequest.getCarId()));

        if (!car.isAvailable()) {
            throw new IllegalStateException("Car is not available for booking");
        }

        // Check for overlapping bookings
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                car.getId(), bookingRequest.getStartDate(), bookingRequest.getEndDate());

        if (!overlappingBookings.isEmpty()) {
            throw new IllegalStateException("Car is already booked for the selected dates");
        }

        // Validate dates
        if (bookingRequest.getStartDate().isAfter(bookingRequest.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        // Calculate total price
        long days = ChronoUnit.DAYS.between(bookingRequest.getStartDate(), bookingRequest.getEndDate()) + 1;
        BigDecimal totalPrice = car.getPricePerDay().multiply(BigDecimal.valueOf(days));

        Booking booking = Booking.builder()
                .user(user)
                .car(car)
                .startDate(bookingRequest.getStartDate())
                .endDate(bookingRequest.getEndDate())
                .totalPrice(totalPrice)
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // Send confirmation email asynchronously - won't block the booking process
        try {
            String bookingDetails = "<p><strong>Car:</strong> " + car.getMake() + " " + car.getModel() + "</p>"
                    + "<p><strong>Dates:</strong> " + bookingRequest.getStartDate() + " to " + bookingRequest.getEndDate() + "</p>"
                    + "<p><strong>Total Price:</strong> $" + totalPrice + "</p>";
            emailService.sendBookingConfirmationEmail(user.getEmail(), bookingDetails);
        } catch (Exception e) {
            // Log the error but don't fail the booking
            log.error("Failed to send booking confirmation email", e);
        }

        return mapToBookingResponse(savedBooking);
    }

    @Transactional
    public BookingResponse updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        booking.setStatus(status);

        // If booking is cancelled, make the car available again
        if (status == BookingStatus.CANCELLED) {
            Car car = booking.getCar();
            car.setAvailable(true);
            carRepository.save(car);
        }

        Booking updatedBooking = bookingRepository.save(booking);
        return mapToBookingResponse(updatedBooking);
    }

    @Transactional
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Booking not found with id: " + id);
        }
        bookingRepository.deleteById(id);
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        UserResponse userResponse = UserResponse.builder()
                .id(booking.getUser().getId())
                .name(booking.getUser().getName())
                .email(booking.getUser().getEmail())
                .role(booking.getUser().getRole())
                .build();

        CarResponse carResponse = CarResponse.builder()
                .id(booking.getCar().getId())
                .make(booking.getCar().getMake())
                .model(booking.getCar().getModel())
                .year(booking.getCar().getYear())
                .pricePerDay(booking.getCar().getPricePerDay())
                .available(booking.getCar().isAvailable())
                .imageUrl(booking.getCar().getImageUrl())
                .licensePlate(booking.getCar().getLicensePlate())
                .color(booking.getCar().getColor())
                .transmission(booking.getCar().getTransmission())
                .seats(booking.getCar().getSeats())
                .fuelType(booking.getCar().getFuelType())
                .build();

        return BookingResponse.builder()
                .id(booking.getId())
                .user(userResponse)
                .car(carResponse)
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
