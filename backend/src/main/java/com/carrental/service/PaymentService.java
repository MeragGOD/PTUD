package com.carrental.service;

import com.carrental.dto.request.PaymentRequest;
import com.carrental.dto.response.PaymentResponse;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.model.*;
import com.carrental.repository.BookingRepository;
import com.carrental.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return mapToPaymentResponse(payment);
    }

    public PaymentResponse getPaymentByBookingId(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        Payment payment = paymentRepository.findByBooking(booking)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking id: " + bookingId));

        return mapToPaymentResponse(payment);
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        Booking booking = bookingRepository.findById(paymentRequest.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + paymentRequest.getBookingId()));

        // Check if payment already exists
        if (paymentRepository.findByBooking(booking).isPresent()) {
            throw new IllegalStateException("Payment already exists for this booking");
        }

        // Simulate payment processing
        boolean paymentSuccessful = simulatePaymentProcessing(paymentRequest);

        PaymentStatus status = paymentSuccessful ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalPrice())
                .paymentStatus(status)
                .transactionId(UUID.randomUUID().toString())
                .paymentDate(LocalDateTime.now())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Update booking status if payment is successful
        if (paymentSuccessful) {
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
        }

        return mapToPaymentResponse(savedPayment);
    }

    @Transactional
    public PaymentResponse refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only completed payments can be refunded");
        }

        // Simulate refund processing
        boolean refundSuccessful = simulateRefundProcessing(payment);

        if (refundSuccessful) {
            payment.setPaymentStatus(PaymentStatus.REFUNDED);
            Payment updatedPayment = paymentRepository.save(payment);

            // Update booking status
            Booking booking = payment.getBooking();
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            return mapToPaymentResponse(updatedPayment);
        } else {
            throw new IllegalStateException("Refund processing failed");
        }
    }

    private boolean simulatePaymentProcessing(PaymentRequest paymentRequest) {
        // In a real application, this would integrate with a payment gateway
        // For this example, we'll simulate a successful payment
        return true;
    }

    private boolean simulateRefundProcessing(Payment payment) {
        // In a real application, this would integrate with a payment gateway
        // For this example, we'll simulate a successful refund
        return true;
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBooking().getId())
                .amount(payment.getAmount())
                .paymentStatus(payment.getPaymentStatus())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .build();
    }
}
