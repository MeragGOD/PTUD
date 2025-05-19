package com.carrental.controller;

import com.carrental.dto.request.BookingRequest;
import com.carrental.dto.response.BookingResponse;
import com.carrental.model.BookingStatus;
import com.carrental.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Booking management API")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all bookings (Admin only)")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @bookingSecurity.isOwner(authentication, #id)")
    @Operation(summary = "Get booking by ID (Admin or owner)")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/my-bookings")
    @Operation(summary = "Get current user's bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(Authentication authentication) {
        List<BookingResponse> bookings = bookingService.getBookingsByUser(authentication.getName());
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/car/{carId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get bookings by car ID (Admin only)")
    public ResponseEntity<List<BookingResponse>> getBookingsByCar(@PathVariable Long carId) {
        List<BookingResponse> bookings = bookingService.getBookingsByCar(carId);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping
    @Operation(summary = "Create a new booking")
    public ResponseEntity<BookingResponse> createBooking(
            Authentication authentication,
            @Valid @RequestBody BookingRequest bookingRequest) {
        BookingResponse booking = bookingService.createBooking(authentication.getName(), bookingRequest);
        return ResponseEntity.ok(booking);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @bookingSecurity.isOwner(authentication, #id)")
    @Operation(summary = "Update booking status (Admin or owner)")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {
        BookingResponse booking = bookingService.updateBookingStatus(id, status);
        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete booking (Admin only)")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
