package com.carrental.controller;

import com.carrental.dto.request.PaymentRequest;
import com.carrental.dto.response.PaymentResponse;
import com.carrental.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management API")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all payments (Admin only)")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @paymentSecurity.isOwner(authentication, #id)")
    @Operation(summary = "Get payment by ID (Admin or owner)")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        PaymentResponse payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasRole('ADMIN') or @bookingSecurity.isOwner(authentication, #bookingId)")
    @Operation(summary = "Get payment by booking ID (Admin or booking owner)")
    public ResponseEntity<PaymentResponse> getPaymentByBookingId(@PathVariable Long bookingId) {
        PaymentResponse payment = paymentService.getPaymentByBookingId(bookingId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/process")
    @Operation(summary = "Process payment for a booking")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        PaymentResponse payment = paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Refund payment (Admin only)")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long id) {
        PaymentResponse payment = paymentService.refundPayment(id);
        return ResponseEntity.ok(payment);
    }
}
